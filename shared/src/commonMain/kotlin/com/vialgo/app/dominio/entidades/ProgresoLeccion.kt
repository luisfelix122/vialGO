package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class ProgresoLeccion(
    val id: String,
    val usuarioId: String,
    val leccionId: String,
    val rol: String,
    val completada: Boolean,
    val estrellas: Int,
    val mejorXp: Int,
    val completadaEn: Instant?,
    val actualizadoEn: Instant,
)
