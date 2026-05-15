package com.vialgo.app.datos.mapeadores

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.datos.dtos.SesionDto
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.entidades.Sesion
import kotlinx.datetime.Instant

fun SesionDto.aEntidad(): Sesion = Sesion(
    id = id,
    usuarioId = usuarioId,
    leccionId = leccionId,
    rol = rol,
    tipo = tipo,
    estado = estado,
    fueMinimizada = fueMinimizada,
    xpGanado = xpGanado,
    preguntasTotales = preguntasTotales,
    iniciadaEn = if (iniciadaEn.isNotEmpty()) Instant.parse(iniciadaEn) else Instant.fromEpochMilliseconds(0),
    completadaEn = completadaEn?.let { Instant.parse(it) },
)

fun Sesion.aDto(): SesionDto = SesionDto(
    id = id,
    usuarioId = usuarioId,
    leccionId = leccionId,
    rol = rol,
    tipo = tipo,
    estado = estado,
    fueMinimizada = fueMinimizada,
    xpGanado = xpGanado,
    preguntasTotales = preguntasTotales,
    iniciadaEn = iniciadaEn.toString(),
    completadaEn = completadaEn?.toString(),
)

fun RespuestaSesionDto.aEntidad(): RespuestaUsuario = RespuestaUsuario(
    id = id,
    sesionId = sesionId,
    preguntaId = preguntaId,
    opcionId = opcionId,
    fueCorrecta = fueCorrecta,
    tiempoRespuestaMs = tiempoRespuestaMs,
    xpObtenido = xpObtenido ?: 0,
    esReintento = esReintento,
)

fun RespuestaUsuario.aDto(sesionId: String): RespuestaSesionDto = RespuestaSesionDto(
    id = id,
    sesionId = sesionId,
    preguntaId = preguntaId,
    opcionId = opcionId,
    fueCorrecta = fueCorrecta,
    tiempoRespuestaMs = tiempoRespuestaMs,
    xpObtenido = xpObtenido,
    esReintento = esReintento,
)
