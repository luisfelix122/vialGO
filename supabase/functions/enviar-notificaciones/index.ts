import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "jsr:@supabase/supabase-js@2";

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type, x-cron-secret",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Connection": "keep-alive",
};

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...CORS_HEADERS, "Content-Type": "application/json" },
  });
}

// ---------------------------------------------------------------------------
// Google OAuth2 — generate access token from service-account JSON
// ---------------------------------------------------------------------------

interface CuentaServicio {
  client_email: string;
  private_key: string;
  project_id: string;
}

function base64url(data: ArrayBuffer | string): string {
  const raw =
    typeof data === "string"
      ? btoa(data)
      : btoa(String.fromCharCode(...new Uint8Array(data)));
  return raw.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

async function obtenerTokenAccesoGoogle(
  sa: CuentaServicio,
): Promise<string> {
  const ahora = Math.floor(Date.now() / 1000);

  const header = base64url(JSON.stringify({ alg: "RS256", typ: "JWT" }));
  const claims = base64url(
    JSON.stringify({
      iss: sa.client_email,
      scope: "https://www.googleapis.com/auth/firebase.messaging",
      aud: "https://oauth2.googleapis.com/token",
      iat: ahora,
      exp: ahora + 3600,
    }),
  );

  const sinFirma = `${header}.${claims}`;

  // Import RSA private key (PKCS#8 PEM → CryptoKey)
  const pemBody = sa.private_key
    .replace(/-----BEGIN PRIVATE KEY-----/, "")
    .replace(/-----END PRIVATE KEY-----/, "")
    .replace(/\s/g, "");
  const binaryKey = Uint8Array.from(atob(pemBody), (c) => c.charCodeAt(0));

  const cryptoKey = await crypto.subtle.importKey(
    "pkcs8",
    binaryKey,
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"],
  );

  const firma = await crypto.subtle.sign(
    "RSASSA-PKCS1-v1_5",
    cryptoKey,
    new TextEncoder().encode(sinFirma),
  );

  const jwt = `${sinFirma}.${base64url(firma)}`;

  const resp = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=${jwt}`,
  });

  if (!resp.ok) {
    throw new Error(
      `Google OAuth2 failed: ${resp.status} ${await resp.text()}`,
    );
  }

  const data = await resp.json();
  return data.access_token;
}

// ---------------------------------------------------------------------------
// FCM v1 API — send a single push notification
// ---------------------------------------------------------------------------

async function enviarFCM(
  token: string,
  titulo: string,
  cuerpo: string,
  accessToken: string,
  projectId: string,
): Promise<{ ok: boolean; noRegistrado: boolean }> {
  const resp = await fetch(
    `https://fcm.googleapis.com/v1/projects/${projectId}/messages:send`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        message: {
          token,
          notification: { title: titulo, body: cuerpo },
          android: {
            priority: "high",
            notification: { channel_id: "recordatorio_diario" },
          },
        },
      }),
    },
  );

  if (resp.ok) return { ok: true, noRegistrado: false };

  const error = await resp.json().catch(() => null);
  const noRegistrado =
    error?.error?.details?.some(
      (d: { errorCode?: string }) => d.errorCode === "UNREGISTERED",
    ) ?? false;

  return { ok: false, noRegistrado };
}

// ---------------------------------------------------------------------------
// Main handler — triggered by external cron at 00:00, 02:00, 04:00 UTC
// (19:00, 21:00, 23:00 Peru time / UTC-5)
// ---------------------------------------------------------------------------

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS_HEADERS });
  }

  if (req.method !== "POST") {
    return jsonResponse(
      { error: "Method not allowed", code: "METHOD_NOT_ALLOWED" },
      405,
    );
  }

  // --- Validate cron secret ---
  const cronSecret = Deno.env.get("CRON_SECRET");
  const incomingSecret = req.headers.get("x-cron-secret");
  if (!cronSecret || !incomingSecret || incomingSecret !== cronSecret) {
    return jsonResponse({ error: "Unauthorized", code: "UNAUTHORIZED" }, 401);
  }

  // --- Parse Firebase service account ---
  const saJson = Deno.env.get("FIREBASE_SERVICE_ACCOUNT");
  if (!saJson) {
    return jsonResponse(
      { error: "FIREBASE_SERVICE_ACCOUNT not configured", code: "CONFIG_ERROR" },
      500,
    );
  }

  let sa: CuentaServicio;
  try {
    sa = JSON.parse(saJson);
  } catch {
    return jsonResponse(
      { error: "Invalid FIREBASE_SERVICE_ACCOUNT JSON", code: "CONFIG_ERROR" },
      500,
    );
  }

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  // --- Current hour in Peru (UTC-5), formatted as TIME ---
  const ahoraPeru = new Date(Date.now() - 5 * 60 * 60 * 1000);
  const horaActual = `${String(ahoraPeru.getUTCHours()).padStart(2, "0")}:00:00`;

  // --- 1. Users scheduled for this hour ---
  const { data: notificaciones, error: notifError } = await supabase
    .from("notificaciones_programadas")
    .select("usuario_id")
    .eq("hora_envio", horaActual)
    .eq("esta_activa", true);

  if (notifError) {
    return jsonResponse(
      { error: notifError.message, code: "QUERY_ERROR" },
      500,
    );
  }

  if (!notificaciones?.length) {
    return jsonResponse({
      data: { enviados: 0, fallidos: 0, tokens_eliminados: 0, hora: horaActual },
      message: "No notifications to send",
    });
  }

  const usuarioIds = notificaciones.map((n) => n.usuario_id);

  // --- 2. Filter: active users only ---
  const { data: usuarios, error: userError } = await supabase
    .from("usuarios")
    .select("id, nombre")
    .in("id", usuarioIds)
    .eq("esta_baneado", false)
    .is("eliminado_en", null);

  if (userError || !usuarios?.length) {
    return jsonResponse({
      data: { enviados: 0, fallidos: 0, tokens_eliminados: 0, hora: horaActual },
      message: userError ? userError.message : "No active users",
    });
  }

  // --- 3. Exclude users who already completed a session today ---
  const hoy = ahoraPeru.toISOString().split("T")[0]; // YYYY-MM-DD in Peru TZ
  const activoIds = usuarios.map((u) => u.id);

  const { data: completados } = await supabase
    .from("roles_usuario")
    .select("usuario_id")
    .in("usuario_id", activoIds)
    .eq("sesion_completada_hoy", hoy);

  const completadoSet = new Set(completados?.map((c) => c.usuario_id) ?? []);
  const pendientes = usuarios.filter((u) => !completadoSet.has(u.id));

  if (!pendientes.length) {
    return jsonResponse({
      data: { enviados: 0, fallidos: 0, tokens_eliminados: 0, hora: horaActual },
      message: "All users completed their session today",
    });
  }

  // --- 4. Get FCM tokens for pending users ---
  const pendienteIds = pendientes.map((u) => u.id);
  const { data: tokens, error: tokenError } = await supabase
    .from("tokens_dispositivo")
    .select("id, usuario_id, token")
    .in("usuario_id", pendienteIds);

  if (tokenError || !tokens?.length) {
    return jsonResponse({
      data: { enviados: 0, fallidos: 0, tokens_eliminados: 0, hora: horaActual },
      message: "No FCM tokens found",
    });
  }

  // --- 5. Get Google access token (one per invocation) ---
  let accessToken: string;
  try {
    accessToken = await obtenerTokenAccesoGoogle(sa);
  } catch (err) {
    return jsonResponse(
      { error: (err as Error).message, code: "OAUTH_ERROR" },
      500,
    );
  }

  // --- 6. Send push notifications ---
  const nombreMap = new Map(pendientes.map((u) => [u.id, u.nombre]));
  let enviados = 0;
  let fallidos = 0;
  const tokensAEliminar: number[] = [];

  // Process concurrently in batches of 10
  const BATCH_SIZE = 10;
  for (let i = 0; i < tokens.length; i += BATCH_SIZE) {
    const lote = tokens.slice(i, i + BATCH_SIZE);

    const resultados = await Promise.allSettled(
      lote.map((t) =>
        enviarFCM(
          t.token,
          "No pierdas tu racha!",
          `${nombreMap.get(t.usuario_id) ?? "Hey"}, aun no completas tu sesion de hoy. Entrena tus reflejos viales!`,
          accessToken,
          sa.project_id,
        )
      ),
    );

    for (let j = 0; j < resultados.length; j++) {
      const r = resultados[j];
      if (r.status === "fulfilled" && r.value.ok) {
        enviados++;
      } else {
        fallidos++;
        if (r.status === "fulfilled" && r.value.noRegistrado) {
          tokensAEliminar.push(lote[j].id);
        }
      }
    }
  }

  // --- 7. Clean up expired/unregistered tokens ---
  if (tokensAEliminar.length > 0) {
    await supabase
      .from("tokens_dispositivo")
      .delete()
      .in("id", tokensAEliminar);
  }

  return jsonResponse({
    data: {
      enviados,
      fallidos,
      tokens_eliminados: tokensAEliminar.length,
      hora: horaActual,
      ejecutado_en: new Date().toISOString(),
    },
    message: "Notifications processed",
  });
});
