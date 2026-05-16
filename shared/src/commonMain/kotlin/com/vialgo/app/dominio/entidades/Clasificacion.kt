package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class Clasificacion(
    val id: String,
    val usuarioId: String,
    val rol: String,
    val sesionId: String,
    val reputacionInicial: Double,
    val completadaEn: Instant?,
)
