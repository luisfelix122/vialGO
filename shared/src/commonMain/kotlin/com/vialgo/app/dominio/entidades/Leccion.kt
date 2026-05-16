package com.vialgo.app.dominio.entidades

data class Leccion(
    val id: String,
    val moduloId: String,
    val nombre: String,
    val descripcion: String,
    val orden: Int,
    val estaActiva: Boolean,
)
