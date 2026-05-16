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

  const {
    dni,
    password,
    nombre,
    pregunta_seguridad,
    respuesta_seguridad,
    rol_activo,
    compromiso_minutos,
  } = body as Record<string, unknown>;

  // --- Validation ---
  if (typeof dni !== "string" || !/^\d{8}$/.test(dni)) {
    return jsonResponse({ error: "DNI must be exactly 8 numeric digits", code: "INVALID_DNI" }, 400);
  }
  if (typeof password !== "string" || password.length < 6) {
    return jsonResponse({ error: "Password must be at least 6 characters", code: "INVALID_PASSWORD" }, 400);
  }
  if (typeof nombre !== "string" || nombre.trim().length === 0) {
    return jsonResponse({ error: "Name is required", code: "INVALID_NOMBRE" }, 400);
  }
  if (typeof pregunta_seguridad !== "string" || pregunta_seguridad.trim().length === 0) {
    return jsonResponse({ error: "Security question is required", code: "INVALID_PREGUNTA" }, 400);
  }
  if (typeof respuesta_seguridad !== "string" || respuesta_seguridad.trim().length === 0) {
    return jsonResponse({ error: "Security answer is required", code: "INVALID_RESPUESTA" }, 400);
  }
  if (typeof rol_activo !== "string" || !["conductor", "peaton"].includes(rol_activo)) {
    return jsonResponse({ error: "rol_activo must be conductor or peaton", code: "INVALID_ROL" }, 400);
  }
  const compromisoNum = Number(compromiso_minutos);
  if (!compromisoNum || ![5, 10, 15, 20, 30].includes(compromisoNum)) {
    return jsonResponse({ error: "compromiso_minutos must be 5, 10, 15, 20, or 30", code: "INVALID_COMPROMISO" }, 400);
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  // --- Check DNI not already registered ---
  const { data: existingUser, error: checkError } = await supabase
    .from("usuarios")
    .select("id")
    .eq("dni", dni)
    .maybeSingle();

  if (checkError) {
    return jsonResponse({ error: "Error checking DNI availability", code: "DB_ERROR" }, 500);
  }
  if (existingUser) {
    return jsonResponse({ error: "DNI already registered", code: "DNI_ALREADY_EXISTS" }, 409);
  }

  // --- Hash security answer with bcrypt via pgcrypto ---
  // Normalize: trim and lowercase before hashing
  const normalizedAnswer = (respuesta_seguridad as string).trim().toLowerCase();
  const { data: hashData, error: hashError } = await supabase
    .rpc("hash_respuesta_seguridad", { respuesta: normalizedAnswer });

  if (hashError || !hashData) {
    return jsonResponse({ error: "Error hashing security answer", code: "HASH_ERROR" }, 500);
  }
  const respuestaHash = hashData as string;

  // --- Create Supabase Auth user ---
  const email = `${dni}@vialgo.internal`;
  const { data: authData, error: authError } = await supabase.auth.admin.createUser({
    email,
    password,
    email_confirm: true,
  });

  if (authError) {
    return jsonResponse({ error: authError.message, code: "AUTH_CREATE_ERROR" }, 500);
  }

  const userId = authData.user.id;

  // --- Insert all related records (with compensating rollback on failure) ---
  try {
    const { error: userInsertError } = await supabase.from("usuarios").insert({
      id: userId,
      dni,
      nombre: (nombre as string).trim(),
      pregunta_seguridad: (pregunta_seguridad as string).trim(),
      respuesta_seguridad_hash: respuestaHash,
      rol_activo,
      compromiso_minutos: compromisoNum,
    });
    if (userInsertError) throw new Error(`usuarios: ${userInsertError.message}`);

    const { error: rolError } = await supabase.from("roles_usuario").insert({
      usuario_id: userId,
      rol: rol_activo,
    });
    if (rolError) throw new Error(`roles_usuario: ${rolError.message}`);

    const { error: vidasError } = await supabase.from("vidas_usuario").insert({
      usuario_id: userId,
      vidas_actuales: 5,
    });
    if (vidasError) throw new Error(`vidas_usuario: ${vidasError.message}`);

    const { error: statsError } = await supabase.from("estadisticas_usuario").insert({
      usuario_id: userId,
      rol: rol_activo,
    });
    if (statsError) throw new Error(`estadisticas_usuario: ${statsError.message}`);

    const { error: notifError } = await supabase.from("notificaciones_programadas").insert([
      { usuario_id: userId, hora_envio: "19:00:00", esta_activa: true },
      { usuario_id: userId, hora_envio: "21:00:00", esta_activa: true },
      { usuario_id: userId, hora_envio: "23:00:00", esta_activa: true },
    ]);
    if (notifError) throw new Error(`notificaciones_programadas: ${notifError.message}`);
  } catch (err) {
    // Compensating transaction: delete the auth user so the DNI is free again
    await supabase.auth.admin.deleteUser(userId);
    return jsonResponse(
      { error: `Registration failed: ${(err as Error).message}`, code: "REGISTRATION_ERROR" },
      500,
    );
  }

  // --- Sign in and return session ---
  const { data: signInData, error: signInError } = await supabase.auth.signInWithPassword({
    email,
    password,
  });

  if (signInError) {
    return jsonResponse({ error: "User created but sign-in failed", code: "SIGNIN_ERROR" }, 500);
  }

  return jsonResponse({
    data: {
      session: signInData.session,
      user: {
        id: userId,
        dni,
        nombre: (nombre as string).trim(),
        rol_activo,
        compromiso_minutos: compromisoNum,
        debe_cambiar_pregunta: false,
        tutorial_completado: false,
      },
    },
    message: "Registration successful",
  }, 201);
});
