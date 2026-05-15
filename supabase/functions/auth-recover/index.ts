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

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS_HEADERS });
  }

  if (req.method !== "POST") {
    return jsonResponse({ error: "Method not allowed", code: "METHOD_NOT_ALLOWED" }, 405);
  }

  let body: Record<string, unknown>;
  try {
    body = await req.json();
  } catch {
    return jsonResponse({ error: "Invalid JSON body", code: "INVALID_BODY" }, 400);
  }

  const { dni, respuesta_seguridad, nueva_password } = body as Record<string, unknown>;

  // --- Validation ---
  if (typeof dni !== "string" || !/^\d{8}$/.test(dni)) {
    return jsonResponse({ error: "DNI must be exactly 8 numeric digits", code: "INVALID_DNI" }, 400);
  }
  if (typeof respuesta_seguridad !== "string" || respuesta_seguridad.trim().length === 0) {
    return jsonResponse({ error: "Security answer is required", code: "INVALID_RESPUESTA" }, 400);
  }
  if (typeof nueva_password !== "string" || nueva_password.length < 6) {
    return jsonResponse({ error: "New password must be at least 6 characters", code: "INVALID_PASSWORD" }, 400);
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  // --- Rate limiting: same as login (5 attempts / 15 min per DNI) ---
  const { data: intentoRow, error: intentosError } = await supabase
    .from("intentos_auth")
    .select("intentos, ventana_inicio")
    .eq("dni", dni)
    .maybeSingle();

  if (intentosError) {
    return jsonResponse({ error: "Rate limit check failed", code: "DB_ERROR" }, 500);
  }

  if (intentoRow) {
    const ventanaMs = Date.now() - new Date(intentoRow.ventana_inicio).getTime();
    if (ventanaMs > 15 * 60 * 1000) {
      await supabase.from("intentos_auth").delete().eq("dni", dni);
    } else if (intentoRow.intentos >= 5) {
      return jsonResponse(
        { error: "Too many failed attempts. Try again in 15 minutes.", code: "RATE_LIMITED" },
        429,
      );
    }
  }

  // --- Find user by DNI ---
  const { data: usuario, error: userError } = await supabase
    .from("usuarios")
    .select("id, respuesta_seguridad_hash")
    .eq("dni", dni)
    .is("eliminado_en", null)
    .maybeSingle();

  if (userError) {
    return jsonResponse({ error: "Error looking up user", code: "DB_ERROR" }, 500);
  }
  if (!usuario) {
    return jsonResponse({ error: "DNI not found", code: "USER_NOT_FOUND" }, 404);
  }

  // --- Verify security answer via pgcrypto crypt() ---
  const normalizedAnswer = (respuesta_seguridad as string).trim().toLowerCase();
  const { data: verifyData, error: verifyError } = await supabase
    .rpc("verificar_respuesta_seguridad", {
      respuesta_ingresada: normalizedAnswer,
      hash_guardado: usuario.respuesta_seguridad_hash,
    });

  if (verifyError) {
    return jsonResponse({ error: "Error verifying security answer", code: "VERIFY_ERROR" }, 500);
  }

  const esCorrecta = verifyData as boolean;

  if (!esCorrecta) {
    // Record failed attempt (upsert: increment or insert)
    await supabase.rpc("registrar_intento_auth", { p_dni: dni });
    return jsonResponse({ error: "Incorrect security answer", code: "WRONG_ANSWER" }, 401);
  }

  // --- Update password via admin API ---
  const { error: updateError } = await supabase.auth.admin.updateUserById(usuario.id, {
    password: nueva_password,
  });

  if (updateError) {
    return jsonResponse({ error: "Error updating password", code: "PASSWORD_UPDATE_ERROR" }, 500);
  }

  // --- Reset intentos_auth on success ---
  await supabase.from("intentos_auth").delete().eq("dni", dni);

  return jsonResponse({ data: null, message: "Password updated successfully" });
});
