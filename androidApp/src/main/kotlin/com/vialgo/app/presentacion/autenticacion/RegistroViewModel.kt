package com.vialgo.app.presentacion.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRegistro
import com.vialgo.app.dominio.casosdeuso.autenticacion.RegistrarUsuarioUseCase
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.ViewModelBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EstadoRegistro(
    val dni: String = "",
    val contrasena: String = "",
    val nombre: String = "",
    val preguntaSeguridad: String = "",
    val respuestaSeguridad: String = "",
    val rolActivo: String = "conductor",
    val compromisoMinutos: Int = 15,
    val errorGeneral: String? = null,
)

class RegistroViewModel(
    private val registrarUsuario: RegistrarUsuarioUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoRegistro>(EstadoRegistro()) {

    private val _eventos = MutableSharedFlow<EventoNavegacion>()
    val eventos: SharedFlow<EventoNavegacion> = _eventos.asSharedFlow()

    fun onDniCambiado(valor: String) {
        actualizarEstado { it.copy(dni = valor, errorGeneral = null) }
    }

    fun onContrasenaCambiada(valor: String) {
        actualizarEstado { it.copy(contrasena = valor, errorGeneral = null) }
    }

    fun onNombreCambiado(valor: String) {
        actualizarEstado { it.copy(nombre = valor, errorGeneral = null) }
    }

    fun onPreguntaSeguridadCambiada(valor: String) {
        actualizarEstado { it.copy(preguntaSeguridad = valor, errorGeneral = null) }
    }

    fun onRespuestaSeguridadCambiada(valor: String) {
        actualizarEstado { it.copy(respuestaSeguridad = valor, errorGeneral = null) }
    }

    fun onRolActivoCambiado(valor: String) {
        actualizarEstado { it.copy(rolActivo = valor) }
    }

    fun onCompromisoMinutosCambiado(valor: Int) {
        actualizarEstado { it.copy(compromisoMinutos = valor) }
    }

    fun onRegistrar() {
        val estadoActual = (estadoUi.value as? EstadoUi.Contenido)?.datos ?: return

        scope.launch {
            mostrarCargando()

            val resultado = registrarUsuario.ejecutar(
                ParamsRegistro(
                    dni = estadoActual.dni,
                    contrasena = estadoActual.contrasena,
                    nombre = estadoActual.nombre,
                    preguntaSeguridad = estadoActual.preguntaSeguridad,
                    respuestaSeguridad = estadoActual.respuestaSeguridad,
                    rolActivo = estadoActual.rolActivo,
                    compromisoMinutos = estadoActual.compromisoMinutos,
                )
            )

            when (resultado) {
                is Resultado.Exito -> {
                    mostrarContenido(EstadoRegistro())
                    _eventos.emit(EventoNavegacion.IrAOnboarding)
                }
                is Resultado.Error -> {
                    mostrarContenido(
                        estadoActual.copy(errorGeneral = resultado.mensaje)
                    )
                }
                is Resultado.Cargando -> Unit
            }
        }
    }
}
