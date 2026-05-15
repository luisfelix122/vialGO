package com.vialgo.app.datos.mapeadores

import com.vialgo.app.datos.dtos.BeneficioDto
import com.vialgo.app.datos.dtos.ClasificacionDto
import com.vialgo.app.datos.dtos.VidaDto
import com.vialgo.app.dominio.entidades.Beneficio
import com.vialgo.app.dominio.entidades.Clasificacion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Vida
import kotlinx.datetime.Instant

fun VidaDto.aEntidad(): Vida = Vida(
    id = id,
    usuarioId = usuarioId,
    cantidad = cantidad,
    proximaRecargaEn = proximaRecargaEn?.let { Instant.parse(it) },
)

fun BeneficioDto.aEntidad(): Beneficio = Beneficio(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    urlImagen = urlImagen,
    puntosRequeridos = puntosRequeridos,
    categoria = categoria,
    disponible = disponible,
)

fun ClasificacionDto.aEntidad(): Clasificacion = Clasificacion(
    posicion = posicion,
    usuarioId = usuarioId,
    nombreUsuario = nombreUsuario,
    puntaje = puntaje,
    nivel = nivel,
    rolUsuario = rolStringAEntidad(rolUsuario),
)
