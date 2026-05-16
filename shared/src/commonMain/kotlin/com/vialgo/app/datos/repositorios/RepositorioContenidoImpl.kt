package com.vialgo.app.datos.repositorios

import com.vialgo.app.datos.dtos.LeccionDto
import com.vialgo.app.datos.dtos.ModuloDto
import com.vialgo.app.datos.dtos.OpcionPreguntaDto
import com.vialgo.app.datos.dtos.PreguntaDto
import com.vialgo.app.datos.dtos.ProgresoLeccionDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Leccion
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.repositorios.RepositorioContenido
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class RepositorioContenidoImpl(
    private val cliente: SupabaseClient,
) : RepositorioContenido {

    override suspend fun obtenerModulos(rol: RolUsuario): Resultado<List<Modulo>> = try {
        val rolStr = when (rol) {
            RolUsuario.CONDUCTOR -> "conductor"
            RolUsuario.PEATONAL -> "peaton"
            RolUsuario.CICLISTA -> "ciclista"
            RolUsuario.INVITADO -> "invitado"
        }
        val modulosResultado = cliente.postgrest.from("modulos").select {
            filter { eq("rol", rolStr) }
        }
        val modulosDtos = modulosResultado.decodeList<ModuloDto>()

        val moduloIds = modulosDtos.map { it.id }
        val lecciones = if (moduloIds.isNotEmpty()) {
            val leccionesResultado = cliente.postgrest.from("lecciones").select {
                filter { isIn("modulo_id", moduloIds) }
            }
            leccionesResultado.decodeList<LeccionDto>()
        } else {
            emptyList()
        }

        val lecconesPorModulo = lecciones.groupBy { it.moduloId }
        val modulos = modulosDtos.map { moduloDto ->
            val leccionesDeModulo = lecconesPorModulo[moduloDto.id]
                ?.map { it.aEntidad() }
                ?: emptyList()
            moduloDto.aEntidad(leccionesDeModulo)
        }
        Resultado.Exito(modulos)
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener módulos", e)
    }

    override suspend fun obtenerModulo(moduloId: String): Resultado<Modulo> = try {
        val moduloResultado = cliente.postgrest.from("modulos").select {
            filter { eq("id", moduloId) }
        }
        val moduloDto = moduloResultado.decodeSingle<ModuloDto>()

        val leccionesResultado = cliente.postgrest.from("lecciones").select {
            filter { eq("modulo_id", moduloId) }
        }
        val lecciones = leccionesResultado.decodeList<LeccionDto>().map { it.aEntidad() }
        Resultado.Exito(moduloDto.aEntidad(lecciones))
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener módulo", e)
    }

    override suspend fun obtenerLeccion(leccionId: String): Resultado<Leccion> = try {
        val resultado = cliente.postgrest.from("lecciones").select {
            filter { eq("id", leccionId) }
        }
        val dto = resultado.decodeSingle<LeccionDto>()
        Resultado.Exito(dto.aEntidad())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener lección", e)
    }

    override suspend fun obtenerPreguntas(leccionId: String): Resultado<List<Pregunta>> = try {
        val preguntasResultado = cliente.postgrest.from("preguntas").select {
            filter { eq("leccion_id", leccionId) }
        }
        val preguntasDtos = preguntasResultado.decodeList<PreguntaDto>()
        Resultado.Exito(ensamblarPreguntas(preguntasDtos))
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener preguntas", e)
    }

    override suspend fun obtenerPreguntasPorCategoria(categoria: String): Resultado<List<Pregunta>> = try {
        val preguntasResultado = cliente.postgrest.from("preguntas").select {
            filter {
                eq("categoria_id", categoria)
                eq("es_clasificacion", true)
            }
        }
        val preguntasDtos = preguntasResultado.decodeList<PreguntaDto>()
        Resultado.Exito(ensamblarPreguntas(preguntasDtos))
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener preguntas por categoría", e)
    }

    override suspend fun obtenerProgreso(usuarioId: String, rolId: String): Resultado<List<ProgresoLeccion>> = try {
        val resultado = cliente.postgrest.from("progreso_lecciones").select {
            filter {
                eq("usuario_id", usuarioId)
                eq("rol", rolId)
            }
        }
        val dtos = resultado.decodeList<ProgresoLeccionDto>()
        Resultado.Exito(dtos.map { it.aEntidad() })
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener progreso", e)
    }

    private suspend fun ensamblarPreguntas(preguntasDtos: List<PreguntaDto>): List<Pregunta> {
        if (preguntasDtos.isEmpty()) return emptyList()

        val preguntaIds = preguntasDtos.map { it.id }
        val opcionesResultado = cliente.postgrest.from("opciones_pregunta").select {
            filter { isIn("pregunta_id", preguntaIds) }
        }
        val opcionesDtos = opcionesResultado.decodeList<OpcionPreguntaDto>()
        val opcionesPorPregunta = opcionesDtos.groupBy { it.preguntaId }

        return preguntasDtos.map { preguntaDto ->
            val opciones = opcionesPorPregunta[preguntaDto.id]
                ?.map { it.aEntidad() }
                ?: emptyList()
            preguntaDto.aEntidad(opciones)
        }
    }
}
