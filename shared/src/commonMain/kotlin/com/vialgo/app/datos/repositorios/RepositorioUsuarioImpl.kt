package com.vialgo.app.datos.repositorios

import com.vialgo.app.datos.dtos.UsuarioTablaDto
import com.vialgo.app.datos.mapeadores.ErrorMapeador
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.EstadisticasUsuario
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioUsuario
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class RepositorioUsuarioImpl(
    private val cliente: SupabaseClient,
) : RepositorioUsuario {

    override suspend fun obtenerUsuario(usuarioId: String): Resultado<Usuario> =
        try {
            val dto = cliente.postgrest.from("usuarios").select {
                filter { eq("id", usuarioId) }
            }.decodeSingle<UsuarioTablaDto>()
            Resultado.Exito(dto.aEntidad())
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun actualizarRol(usuarioId: String, rol: RolUsuario): Resultado<Usuario> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun obtenerProgreso(usuarioId: String): Resultado<List<ProgresoLeccion>> =
        throw NotImplementedError("Implementación pendiente")

    override suspend fun obtenerEstadisticas(usuarioId: String): Resultado<EstadisticasUsuario> =
        throw NotImplementedError("Implementación pendiente")
}
