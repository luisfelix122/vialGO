package com.vialgo.app.dominio.entidades

data class Modulo(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val orden: Int,
    val rol: String,
    val estaActivo: Boolean,
    val lecciones: List<Leccion>,
)
