package com.vialgo.app.dominio.entidades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RolUsuario {
    @SerialName("conductor")
    CONDUCTOR,

    @SerialName("peaton")
    PEATONAL,

    @SerialName("ciclista")
    CICLISTA,

    @SerialName("invitado")
    INVITADO,
}
