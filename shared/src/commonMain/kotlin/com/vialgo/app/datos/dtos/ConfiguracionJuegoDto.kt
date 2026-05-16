package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfiguracionJuegoDto(
    @SerialName("id") val id: String,
    @SerialName("dias_para_multiplicador") val diasParaMultiplicador: Int,
    @SerialName("valor_multiplicador") val valorMultiplicador: Double,
    @SerialName("horas_recarga_vidas") val horasRecargaVidas: Int,
    @SerialName("reputacion_minima_beneficios") val reputacionMinimaBeneficios: Double,
    @SerialName("decay_porcentaje_diario") val decayPorcentajeDiario: Double,
    @SerialName("decay_dias_gracia") val decayDiasGracia: Int,
)
