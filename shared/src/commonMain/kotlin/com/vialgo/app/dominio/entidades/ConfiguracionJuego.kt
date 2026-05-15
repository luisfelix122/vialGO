package com.vialgo.app.dominio.entidades

data class ConfiguracionJuego(
    val vidasMaximas: Int,
    val tiempoRecargaVidaMinutos: Int,
    val puntajePorRespuestaCorrecta: Int,
    val bonificacionRacha: Int,
    val nivelExperienciaPorLeccion: Int,
)
