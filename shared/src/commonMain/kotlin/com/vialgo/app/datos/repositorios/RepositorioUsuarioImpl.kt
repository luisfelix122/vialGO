package com.vialgo.app.datos.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.EstadisticasUsuario
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioUsuario
import io.github.jan.supabase.SupabaseClient

class RepositorioUsuarioImpl(
    private val cliente: SupabaseClient,
) : RepositorioUsuario {

    override suspend fun obtenerUsuario(usuarioId: String): Resultado<Usuario> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun actualizarRol(usuarioId: String, rol: RolUsuario): Resultado<Usuario> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun obtenerProgreso(usuarioId: String): Resultado<List<ProgresoLeccion>> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun obtenerEstadisticas(usuarioId: String): Resultado<EstadisticasUsuario> =
        throw NotImplementedError("Implementación pendiente")
}
