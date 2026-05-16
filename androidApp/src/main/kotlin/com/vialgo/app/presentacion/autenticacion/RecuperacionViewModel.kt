package com.vialgo.app.presentacion.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRecuperacion
import com.vialgo.app.dominio.casosdeuso.autenticacion.RecuperarContrasenaUseCase
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.ViewModelBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EstadoRecuperacion(
    val dni: String = "",
    val respuestaSeguridad: String = "",
    val nuevaContrasena: String = "",
    val exito: Boolean = false,
    val errorGeneral: String? = null,
)

class RecuperacionViewModel(
    private val recuperarContrasena: RecuperarContrasenaUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoRecuperacion>(EstadoRecuperacion()) {

    private val _eventos = MutableSharedFlow<EventoNavegacion>()
    val eventos: SharedFlow<EventoNavegacion> = _eventos.asSharedFlow()

    fun onDniCambiado(valor: String) {
        actualizarEstado { it.copy(dni = valor, errorGeneral = null) }
    }

    fun onRespuestaSeguridadCambiada(valor: String) {
        actualizarEstado { it.copy(respuestaSeguridad = valor, errorGeneral = null) }
    }

    fun onNuevaContrasenaCambiada(valor: String) {
        actualizarEstado { it.copy(nuevaContrasena = valor, errorGeneral = null) }
    }

    fun onRecuperar() {
        val estadoActual = (estadoUi.value as? EstadoUi.Contenido)?.datos ?: return

        scope.launch {
            mostrarCargando()

            val resultado = recuperarContrasena.ejecutar(
                ParamsRecuperacion(
                    dni = estadoActual.dni,
                    respuestaSeguridad = estadoActual.respuestaSeguridad,
                    nuevaContrasena = estadoActual.nuevaContrasena,
                )
            )

            when (resultado) {
                is Resultado.Exito -> {
                    mostrarContenido(estadoActual.copy(exito = true, errorGeneral = null))
                    delay(2500)
                    _eventos.emit(EventoNavegacion.Volver)
                }
                is Resultado.Error -> {
                    mostrarContenido(
                        estadoActual.copy(errorGeneral = resultado.mensaje, exito = false)
                    )
                }
                is Resultado.Cargando -> Unit
            }
        }
    }
}
