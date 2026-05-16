package com.vialgo.app.datos.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val error: String = "",
    val code: String = "",
)
