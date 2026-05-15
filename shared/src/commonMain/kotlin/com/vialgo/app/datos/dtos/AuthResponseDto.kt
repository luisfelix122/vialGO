package com.vialgo.app.datos.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wrapper for Edge Function auth responses.
 * Matches: { data: { session: {...}, user: {...} }, message: "..." }
 */
@Serializable
data class AuthResponseDto(
    @SerialName("data") val data: AuthDataDto? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class AuthDataDto(
    @SerialName("session") val session: SessionAuthDto? = null,
    @SerialName("user") val user: UsuarioAuthDto? = null,
)

@Serializable
data class SessionAuthDto(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("expires_in") val expiresIn: Long = 0,
    @SerialName("token_type") val tokenType: String = "bearer",
)

@Serializable
data class UsuarioAuthDto(
    @SerialName("id") val id: String,
    @SerialName("dni") val dni: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("rol_activo") val rolActivo: String,
    @SerialName("compromiso_minutos") val compromisoMinutos: Int,
    @SerialName("tutorial_completado") val tutorialCompletado: Boolean,
    @SerialName("debe_cambiar_pregunta") val debeCambiarPregunta: Boolean,
)
