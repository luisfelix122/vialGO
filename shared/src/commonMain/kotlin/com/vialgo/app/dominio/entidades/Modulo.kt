package com.vialgo.app.dominio.entidades

data class Modulo(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val orden: Int,
    val urlImagenPortada: String?,
    val rolesDisponibles: List<RolUsuario>,
    val lecciones: List<Leccion>,
)
