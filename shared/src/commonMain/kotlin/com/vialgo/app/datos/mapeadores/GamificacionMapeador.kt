package com.vialgo.app.datos.mapeadores

import com.vialgo.app.datos.dtos.BeneficioDto
import com.vialgo.app.datos.dtos.ClasificacionDto
import com.vialgo.app.datos.dtos.ConfiguracionJuegoDto
import com.vialgo.app.datos.dtos.EstadisticasUsuarioDto
import com.vialgo.app.datos.dtos.RolUsuarioDto
import com.vialgo.app.datos.dtos.VidaDto
import com.vialgo.app.dominio.entidades.Beneficio
import com.vialgo.app.dominio.entidades.Clasificacion
import com.vialgo.app.dominio.entidades.ConfiguracionJuego
import com.vialgo.app.dominio.entidades.EstadisticasUsuario
import com.vialgo.app.dominio.entidades.Vida
import kotlinx.datetime.Instant

private val EPOCH = Instant.fromEpochMilliseconds(0)

fun VidaDto.aEntidad(): Vida = Vida(
    id = id,
    usuarioId = usuarioId,
    vidasActuales = vidasActuales,
    ultimaRecarga = if (ultimaRecarga.isNotBlank()) Instant.parse(ultimaRecarga) else EPOCH,
    actualizadoEn = if (actualizadoEn.isNotBlank()) Instant.parse(actualizadoEn) else EPOCH,
)

fun BeneficioDto.aEntidad(): Beneficio = Beneficio(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    imagenUrl = imagenUrl,
    rol = rol,
    reputacionMinima = reputacionMinima,
    estaActivo = estaActivo,
    disponible = disponible,
    orden = orden,
)

fun ClasificacionDto.aEntidad(): Clasificacion = Clasificacion(
    id = id,
    usuarioId = usuarioId,
    rol = rol,
    sesionId = sesionId,
    reputacionInicial = reputacionInicial,
    completadaEn = if (completadaEn.isNotBlank()) Instant.parse(completadaEn) else null,
)

fun EstadisticasUsuarioDto.aEntidad(): EstadisticasUsuario = EstadisticasUsuario(
    id = id,
    usuarioId = usuarioId,
    rol = rol,
    totalSesiones = totalSesiones,
    totalPreguntas = totalPreguntas,
    totalCorrectas = totalCorrectas,
    tiempoTotalMs = tiempoTotalMs,
    vidasSalvadas = vidasSalvadas,
    actualizadoEn = if (actualizadoEn.isNotBlank()) Instant.parse(actualizadoEn) else EPOCH,
)

fun ConfiguracionJuegoDto.aEntidad(): ConfiguracionJuego = ConfiguracionJuego(
    id = id,
    diasParaMultiplicador = diasParaMultiplicador,
    valorMultiplicador = valorMultiplicador,
    horasRecargaVidas = horasRecargaVidas,
    reputacionMinimaBeneficios = reputacionMinimaBeneficios,
    decayPorcentajeDiario = decayPorcentajeDiario,
    decayDiasGracia = decayDiasGracia,
)
