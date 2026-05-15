package com.vialgo.app.dominio.entidades

data class Leccion(
    val id: String,
    val moduloId: String,
    val titulo: String,
    val descripcion: String,
    val orden: Int,
    val puntajeMaximo: Int,
    val tiempoLimiteSegundos: Int?,
    val urlImagenPortada: String?,
)
