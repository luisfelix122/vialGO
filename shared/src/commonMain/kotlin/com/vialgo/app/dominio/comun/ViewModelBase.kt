package com.vialgo.app.dominio.comun

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base class for ViewModels that manage UI state via [EstadoUi].
 *
 * Feature ViewModels extend this with their concrete state type [T].
 */
abstract class ViewModelBase<T>(estadoInicial: T) {

    private val _estadoUi = MutableStateFlow<EstadoUi<T>>(EstadoUi.Contenido(estadoInicial))
    val estadoUi: StateFlow<EstadoUi<T>> = _estadoUi.asStateFlow()

    protected fun actualizarEstado(transformar: (T) -> T) {
        val actual = _estadoUi.value
        if (actual is EstadoUi.Contenido) {
            _estadoUi.value = EstadoUi.Contenido(transformar(actual.datos))
        }
    }

    protected fun mostrarCargando() {
        _estadoUi.value = EstadoUi.Cargando
    }

    protected fun mostrarContenido(datos: T) {
        _estadoUi.value = EstadoUi.Contenido(datos)
    }

    protected fun mostrarError(mensaje: String) {
        _estadoUi.value = EstadoUi.Error(mensaje)
    }
}
