package com.vialgo.app.presentacion.navegacion

import kotlinx.serialization.Serializable

// ─── Grafo de Autenticación ───────────────────────────────────────────────────

@Serializable
object GrafoAuth

@Serializable
object RutaLogin

@Serializable
object RutaRegistro

@Serializable
object RutaRecuperacion

@Serializable
object RutaInvitado

// ─── Grafo Principal (Bottom Bar) ────────────────────────────────────────────

@Serializable
object GrafoPrincipal

@Serializable
object RutaAprender

@Serializable
object RutaRanking

@Serializable
object RutaBeneficios

@Serializable
object RutaPerfil

// ─── Grafo de Sesión ─────────────────────────────────────────────────────────

@Serializable
object GrafoSesion

@Serializable
object RutaTutorial

@Serializable
object RutaClasificacion

@Serializable
data class RutaSesion(val leccionId: String)

@Serializable
data class RutaResultado(val sesionId: String)

// ─── Grafo de Onboarding ─────────────────────────────────────────────────────

@Serializable
object GrafoOnboarding

@Serializable
object RutaBienvenida

@Serializable
object RutaSeleccionRol

@Serializable
object RutaCompromiso

@Serializable
object RutaTutorialIntro
