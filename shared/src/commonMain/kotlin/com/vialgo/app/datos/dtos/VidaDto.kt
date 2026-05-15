package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VidaDto(
    @SerialName("id") val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("cantidad") val cantidad: Int,
    @SerialName("proxima_recarga_en") val proximaRecargaEn: String?,
)
