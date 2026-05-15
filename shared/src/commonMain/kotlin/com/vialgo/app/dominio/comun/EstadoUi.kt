package com.vialgo.app.dominio.comun

sealed interface EstadoUi<out T> {
    data object Cargando : EstadoUi<Nothing>
    data class Contenido<out T>(val datos: T) : EstadoUi<T>
    data class Error(val mensaje: String) : EstadoUi<Nothing>
}
