import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "jsr:@supabase/supabase-js@2";

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Connection": "keep-alive",
};

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...CORS_HEADERS, "Content-Type": "application/json" },
  });
}

function getAdminDnis(): string[] {
  const raw = Deno.env.get("ADMIN_DNIS") ?? "";
  return raw.split(",").map((s) => s.trim()).filter(Boolean);
}

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS_HEADERS });
  }

  if (req.method !== "POST") {
    return jsonResponse({ error: "Method not allowed", code: "METHOD_NOT_ALLOWED" }, 405);
  }

  // --- Auth: require JWT and verify caller is admin ---
  const authHeader = req.headers.get("Authorization");
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return jsonResponse({ error: "Authentication required", code: "UNAUTHORIZED" }, 401);
  }

  const token = authHeader.slice(7);

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  // Verify the token and get caller identity
  const { data: { user: caller }, error: authError } = await supabase.auth.getUser(token);
  if (authError || !caller) {
    return jsonResponse({ error: "Invalid or expired token", code: "UNAUTHORIZED" }, 401);
  }

  // Look up caller's DNI
  const { data: callerProfile, error: profileError } = await supabase
    .from("usuarios")
    .select("dni")
    .eq("id", caller.id)
    .single();

  if (profileError || !callerProfile) {
    return jsonResponse({ error: "Caller profile not found", code: "UNAUTHORIZED" }, 401);
  }

  const adminDnis = getAdminDnis();
  if (!adminDnis.includes(callerProfile.dni)) {
    return jsonResponse({ error: "Caller is not an admin", code: "FORBIDDEN" }, 403);
  }

  // --- Parse body ---
  let body: Record<string, unknown>;
  try {
    body = await req.json();
  } catch {
    return jsonResponse({ error: "Invalid JSON body", code: "INVALID_BODY" }, 400);
  }

  const { usuario_id } = body as Record<string, unknown>;
  if (typeof usuario_id !== "string" || usuario_id.trim().length === 0) {
    return jsonResponse({ error: "usuario_id is required", code: "INVALID_INPUT" }, 400);
  }

  // --- Verify target user exists ---
  const { data: targetUser, error: targetError } = await supabase
    .from("usuarios")
    .select("id")
    .eq("id", usuario_id)
    .is("eliminado_en", null)
    .maybeSingle();

  if (targetError) {
    return jsonResponse({ error: "Error looking up user", code: "DB_ERROR" }, 500);
  }
  if (!targetUser) {
    return jsonResponse({ error: "User not found", code: "USER_NOT_FOUND" }, 404);
  }

  // --- Set debe_cambiar_pregunta = true ---
  const { error: updateError } = await supabase
    .from("usuarios")
    .update({ debe_cambiar_pregunta: true })
    .eq("id", usuario_id);

  if (updateError) {
    return jsonResponse({ error: "Error updating user flag", code: "UPDATE_ERROR" }, 500);
  }

  return jsonResponse({
    data: { usuario_id },
    message: "User will be prompted to update their security question on next login.",
  });
});
