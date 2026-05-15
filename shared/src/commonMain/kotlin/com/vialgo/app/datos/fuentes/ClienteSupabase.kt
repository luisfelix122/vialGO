package com.vialgo.app.datos.fuentes

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Crea y configura el cliente Supabase singleton.
 * Las credenciales se inyectan desde BuildConfig en androidApp.
 */
fun crearClienteSupabase(
    url: String,
    claveAnonima: String,
): SupabaseClient = createSupabaseClient(
    supabaseUrl = url,
    supabaseKey = claveAnonima,
) {
    install(Auth)
    install(Postgrest)
    install(Functions)
    install(Realtime)
}
