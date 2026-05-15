package com.vialgo.app.presentacion.autenticacion

sealed interface EventoNavegacion {
    data object IrAPrincipal : EventoNavegacion
    data object IrAOnboarding : EventoNavegacion
    data object Volver : EventoNavegacion
}
