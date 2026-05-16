package com.vialgo.app.dominio.entidades

data class Beneficio(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val imagenUrl: String?,
    val rol: String,
    val reputacionMinima: Double,
    val estaActivo: Boolean,
    val disponible: Boolean,
    val orden: Int,
)
