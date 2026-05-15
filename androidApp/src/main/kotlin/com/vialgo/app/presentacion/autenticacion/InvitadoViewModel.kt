package com.vialgo.app.presentacion.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionInvitadoUseCase
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

private const val LIMITE_PREGUNTAS = 5

data class EstadoInvitado(
    val contadorPreguntas: Int = 0,
    val mostrarPromptRegistro: Boolean = false,
    val errorGeneral: String? = null,
)

class InvitadoViewModel(
    private val iniciarSesionInvitado: IniciarSesionInvitadoUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoInvitado>(EstadoInvitado()) {

    private val _eventos = MutableSharedFlow<EventoNavegacion>()
    val eventos: SharedFlow<EventoNavegacion> = _eventos.asSharedFlow()

    fun onIniciarComoInvitado() {
        scope.launch {
            mostrarCargando()

            val resultado = iniciarSesionInvitado.ejecutar()

            when (resultado) {
                is Resultado.Exito -> {
                    mostrarContenido(EstadoInvitado())
                    _eventos.emit(EventoNavegacion.IrAPrincipal)
                }
                is Resultado.Error -> {
                    mostrarContenido(
                        EstadoInvitado(errorGeneral = resultado.mensaje)
                    )
                }
                is Resultado.Cargando -> Unit
            }
        }
    }

    fun onPreguntaRespondida() {
        val estadoActual = (estadoUi.value as? EstadoUi.Contenido)?.datos ?: return
        val nuevoContador = estadoActual.contadorPreguntas + 1
        actualizarEstado {
            it.copy(
                contadorPreguntas = nuevoContador,
                mostrarPromptRegistro = nuevoContador >= LIMITE_PREGUNTAS,
            )
        }
    }

    fun onDescartarPrompt() {
        actualizarEstado { it.copy(mostrarPromptRegistro = false) }
    }
}
