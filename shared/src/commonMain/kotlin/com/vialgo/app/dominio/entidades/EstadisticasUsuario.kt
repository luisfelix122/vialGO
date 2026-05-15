package com.vialgo.app.dominio.entidades

data class EstadisticasUsuario(
    val usuarioId: String,
    val leccionesCompletadas: Int,
    val sesionesTotales: Int,
    val tiempoTotalSegundos: Int,
    val puntajeTotalAcumulado: Int,
    val rachaActual: Int,
    val rachaMasLarga: Int,
    val posicionRanking: Int?,
)
