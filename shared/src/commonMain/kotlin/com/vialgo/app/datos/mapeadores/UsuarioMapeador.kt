package com.vialgo.app.datos.mapeadores

import com.vialgo.app.datos.dtos.UsuarioAuthDto
import com.vialgo.app.datos.dtos.UsuarioDto
import com.vialgo.app.datos.dtos.UsuarioTablaDto
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import kotlinx.datetime.Instant

internal fun rolStringAEntidad(rol: String): RolUsuario = when (rol.lowercase()) {
    "conductor" -> RolUsuario.CONDUCTOR
    "peaton" -> RolUsuario.PEATONAL
    "ciclista" -> RolUsuario.CICLISTA
    "invitado" -> RolUsuario.INVITADO
    else -> RolUsuario.valueOf(rol.uppercase())
}

fun UsuarioDto.aEntidad(): Usuario = Usuario(
    id = id,
    correo = correo,
    nombre = nombre,
    rol = if (rol.isNotBlank()) rolStringAEntidad(rol) else RolUsuario.CONDUCTOR,
    vidas = vidas,
    rachaActual = rachaActual,
    rachaMasLarga = rachaMasLarga,
    puntosExperiencia = puntosExperiencia,
    nivel = nivel,
    creadoEn = if (creadoEn.isNotBlank()) Instant.parse(creadoEn) else Instant.fromEpochMilliseconds(0),
    actualizadoEn = if (actualizadoEn.isNotBlank()) Instant.parse(actualizadoEn) else Instant.fromEpochMilliseconds(0),
    dni = dni,
    preguntaSeguridad = preguntaSeguridad,
    rolActivo = rolActivo,
    compromisoMinutos = compromisoMinutos,
    tutorialCompletado = tutorialCompletado,
    debeCambiarPregunta = debeCambiarPregunta,
)

fun Usuario.aDto(): UsuarioDto = UsuarioDto(
    id = id,
    correo = correo,
    nombre = nombre,
    rol = rol.name.lowercase(),
    vidas = vidas,
    rachaActual = rachaActual,
    rachaMasLarga = rachaMasLarga,
    puntosExperiencia = puntosExperiencia,
    nivel = nivel,
    creadoEn = creadoEn.toString(),
    actualizadoEn = actualizadoEn.toString(),
    dni = dni,
    preguntaSeguridad = preguntaSeguridad,
    rolActivo = rolActivo,
    compromisoMinutos = compromisoMinutos,
    tutorialCompletado = tutorialCompletado,
    debeCambiarPregunta = debeCambiarPregunta,
)

/**
 * Mapea una fila de la tabla `usuarios` al dominio.
 * Los campos de gamificación no existen en esta tabla — se inicializan en cero.
 */
fun UsuarioTablaDto.aEntidad(): Usuario = Usuario(
    id = id,
    correo = "",
    nombre = nombre,
    rol = runCatching { rolStringAEntidad(rolActivo) }.getOrDefault(RolUsuario.CONDUCTOR),
    vidas = 0,
    rachaActual = 0,
    rachaMasLarga = 0,
    puntosExperiencia = 0,
    nivel = 0,
    creadoEn = if (fechaRegistro.isNotBlank()) Instant.parse(fechaRegistro) else Instant.fromEpochMilliseconds(0),
    actualizadoEn = if (actualizadoEn.isNotBlank()) Instant.parse(actualizadoEn) else Instant.fromEpochMilliseconds(0),
    dni = dni,
    rolActivo = rolActivo,
    compromisoMinutos = compromisoMinutos,
    tutorialCompletado = tutorialCompletado,
    debeCambiarPregunta = debeCambiarPregunta,
)

/**
 * Maps the auth response user DTO (from Edge Function) to the domain entity.
 * Gamification fields default to zero since the auth response only has identity fields.
 */
fun UsuarioAuthDto.aEntidad(): Usuario = Usuario(
    id = id,
    correo = "",
    nombre = nombre,
    rol = runCatching { rolStringAEntidad(rolActivo) }.getOrDefault(RolUsuario.CONDUCTOR),
    vidas = 0,
    rachaActual = 0,
    rachaMasLarga = 0,
    puntosExperiencia = 0,
    nivel = 0,
    creadoEn = Instant.fromEpochMilliseconds(0),
    actualizadoEn = Instant.fromEpochMilliseconds(0),
    dni = dni,
    rolActivo = rolActivo,
    compromisoMinutos = compromisoMinutos,
    tutorialCompletado = tutorialCompletado,
    debeCambiarPregunta = debeCambiarPregunta,
)
