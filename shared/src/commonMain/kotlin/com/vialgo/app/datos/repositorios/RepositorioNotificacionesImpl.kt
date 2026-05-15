package com.vialgo.app.datos.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.repositorios.RepositorioNotificaciones
import io.github.jan.supabase.SupabaseClient

class RepositorioNotificacionesImpl(
    private val cliente: SupabaseClient,
) : RepositorioNotificaciones {

    override suspend fun registrarTokenFcm(usuarioId: String, token: String): Resultado<Unit> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun eliminarTokenFcm(usuarioId: String): Resultado<Unit> =
        throw NotImplementedError("Implementación pendiente")
}
