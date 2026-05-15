package com.vialgo.app.datos.repositorios

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.datos.dtos.SesionDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.entidades.Sesion
import com.vialgo.app.dominio.repositorios.RepositorioSesion
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class RepositorioSesionImpl(
    private val cliente: SupabaseClient,
) : RepositorioSesion {

    override suspend fun iniciarSesion(
        usuarioId: String,
        leccionId: String?,
        tipo: String,
        rolId: String,
    ): Resultado<Sesion> = try {
        val dto = SesionDto(
            usuarioId = usuarioId,
            leccionId = leccionId,
            rol = rolId,
            tipo = tipo,
        )
        val resultado = cliente.postgrest.from("sesiones").insert(dto) {
            select()
        }
        val sesionDto = resultado.decodeSingle<SesionDto>()
        Resultado.Exito(sesionDto.aEntidad())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al iniciar sesión de juego", e)
    }

    override suspend fun registrarRespuesta(respuesta: RespuestaSesionDto): Resultado<RespuestaUsuario> = try {
        val resultado = cliente.postgrest.from("respuestas_sesion").insert(respuesta) {
            select()
        }
        val dto = resultado.decodeSingle<RespuestaSesionDto>()
        Resultado.Exito(dto.aEntidad())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al registrar respuesta", e)
    }

    override suspend fun finalizarSesion(sesionId: String, xpGanado: Int): Resultado<Sesion> = try {
        val resultado = cliente.postgrest.from("sesiones").update(
            {
                set("estado", "completada")
                set("xp_ganado", xpGanado)
            }
        ) {
            filter { eq("id", sesionId) }
            select()
        }
        val dto = resultado.decodeSingle<SesionDto>()
        Resultado.Exito(dto.aEntidad())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al finalizar sesión", e)
    }

    override suspend fun obtenerSesion(sesionId: String): Resultado<Sesion> = try {
        val resultado = cliente.postgrest.from("sesiones").select {
            filter { eq("id", sesionId) }
        }
        val dto = resultado.decodeSingle<SesionDto>()
        Resultado.Exito(dto.aEntidad())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener sesión", e)
    }

    override suspend fun obtenerHistorial(usuarioId: String): Resultado<List<Sesion>> = try {
        val resultado = cliente.postgrest.from("sesiones").select {
            filter { eq("usuario_id", usuarioId) }
        }
        val dtos = resultado.decodeList<SesionDto>()
        Resultado.Exito(dtos.map { it.aEntidad() })
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener historial", e)
    }
}
