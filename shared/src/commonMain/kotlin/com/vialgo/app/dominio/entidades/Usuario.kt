package com.vialgo.app.dominio.entidades

import kotlinx.datetime.Instant

data class Usuario(
    val id: String,
    val correo: String,
    val nombre: String,
    val rol: RolUsuario,
    val vidas: Int,
    val rachaActual: Int,
    val rachaMasLarga: Int,
    val puntosExperiencia: Int,
    val nivel: Int,
    val creadoEn: Instant,
    val actualizadoEn: Instant,
    // Auth fields from Edge Function response
    val dni: String = "",
    val preguntaSeguridad: String? = null,
    val rolActivo: String = "",
    val compromisoMinutos: Int = 0,
    val tutorialCompletado: Boolean = false,
    val debeCambiarPregunta: Boolean = false,
)
