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

  const { dni, password } = body as Record<string, unknown>;

  // --- Validation ---
  if (typeof dni !== "string" || !/^\d{8}$/.test(dni)) {
    return jsonResponse({ error: "DNI must be exactly 8 numeric digits", code: "INVALID_DNI" }, 400);
  }
  if (typeof password !== "string" || password.length === 0) {
    return jsonResponse({ error: "Password is required", code: "INVALID_PASSWORD" }, 400);
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  // --- Rate limiting: check intentos_auth ---
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

  // --- Construct synthetic email and sign in ---
  const email = `${dni}@vialgo.internal`;
  const { data: signInData, error: signInError } = await supabase.auth.signInWithPassword({
    email,
    password,
  });

  if (signInError) {
    // Record failed attempt (upsert: increment or insert)
    await supabase.rpc("registrar_intento_auth", { p_dni: dni });
    return jsonResponse({ error: "Invalid DNI or password", code: "INVALID_CREDENTIALS" }, 401);
  }

  // --- Reset intentos_auth on success ---
  await supabase.from("intentos_auth").delete().eq("dni", dni);

  // --- Fetch user profile ---
  const { data: usuario, error: profileError } = await supabase
    .from("usuarios")
    .select(
      "id, dni, nombre, rol_activo, compromiso_minutos, tutorial_completado, debe_cambiar_pregunta, esta_baneado",
    )
    .eq("id", signInData.user.id)
    .single();

  if (profileError || !usuario) {
    return jsonResponse({ error: "User profile not found", code: "PROFILE_NOT_FOUND" }, 500);
  }

  // --- Check banned status ---
  if (usuario.esta_baneado) {
    return jsonResponse({ error: "Account is banned", code: "ACCOUNT_BANNED" }, 403);
  }

  return jsonResponse({
    data: {
      session: signInData.session,
      user: {
        id: usuario.id,
        dni: usuario.dni,
        nombre: usuario.nombre,
        rol_activo: usuario.rol_activo,
        compromiso_minutos: usuario.compromiso_minutos,
        tutorial_completado: usuario.tutorial_completado,
        debe_cambiar_pregunta: usuario.debe_cambiar_pregunta,
      },
    },
  });
});
