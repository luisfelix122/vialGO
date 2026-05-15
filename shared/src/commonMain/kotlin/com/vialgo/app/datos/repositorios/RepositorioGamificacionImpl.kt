package com.vialgo.app.datos.repositorios

import com.vialgo.app.datos.dtos.ClasificacionDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Beneficio
import com.vialgo.app.dominio.entidades.Clasificacion
import com.vialgo.app.dominio.entidades.Vida
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.Json

class RepositorioGamificacionImpl(
    private val cliente: SupabaseClient,
) : RepositorioGamificacion {

    override suspend fun obtenerVidas(usuarioId: String): Resultado<Vida> = try {
        val respuesta = cliente.postgrest.rpc(
            "obtener_vidas",
            buildJsonObject { put("p_usuario_id", JsonPrimitive(usuarioId)) },
        )
        val vidasActuales = respuesta.data.trim().trimStart('"').trimEnd('"').toInt()
        val vida = Vida(
            id = usuarioId,
            usuarioId = usuarioId,
            cantidad = vidasActuales,
            proximaRecargaEn = null,
        )
        Resultado.Exito(vida)
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener vidas", e)
    }

    override suspend fun consumirVida(usuarioId: String): Resultado<Vida> = try {
        cliente.postgrest.rpc(
            "consumir_vida",
            buildJsonObject { put("p_usuario_id", JsonPrimitive(usuarioId)) },
        )
        obtenerVidas(usuarioId)
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al consumir vida", e)
    }

    override suspend fun obtenerClasificacion(limite: Int): Resultado<List<Clasificacion>> = try {
        val resultado = cliente.postgrest.from("clasificaciones").select {
            order("reputacion_inicial", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit(limite.toLong())
        }
        val dtos = resultado.decodeList<ClasificacionDto>()
        Resultado.Exito(dtos.map { it.aEntidad() })
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener clasificación", e)
    }

    override suspend fun obtenerBeneficios(): Resultado<List<Beneficio>> = try {
        Resultado.Exito(emptyList())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al obtener beneficios", e)
    }

    override suspend fun canjearBeneficio(usuarioId: String, beneficioId: String): Resultado<Unit> = try {
        Resultado.Exito(Unit)
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Error al canjear beneficio", e)
    }
}
