package com.vialgo.app.dominio.entidades

data class ConfiguracionJuego(
    val id: String,
    val diasParaMultiplicador: Int,
    val valorMultiplicador: Double,
    val horasRecargaVidas: Int,
    val reputacionMinimaBeneficios: Double,
    val decayPorcentajeDiario: Double,
    val decayDiasGracia: Int,
)
