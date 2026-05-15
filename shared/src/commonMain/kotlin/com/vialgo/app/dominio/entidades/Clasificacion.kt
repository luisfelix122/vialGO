package com.vialgo.app.dominio.entidades

data class Clasificacion(
    val posicion: Int,
    val usuarioId: String,
    val nombreUsuario: String,
    val puntaje: Int,
    val nivel: Int,
    val rolUsuario: RolUsuario,
)
