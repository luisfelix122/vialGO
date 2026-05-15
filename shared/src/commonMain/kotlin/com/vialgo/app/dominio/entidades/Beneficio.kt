package com.vialgo.app.dominio.entidades

data class Beneficio(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val urlImagen: String?,
    val puntosRequeridos: Int,
    val categoria: String,
    val disponible: Boolean,
)
