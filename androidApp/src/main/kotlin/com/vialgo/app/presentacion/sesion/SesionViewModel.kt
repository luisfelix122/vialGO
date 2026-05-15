package com.vialgo.app.presentacion.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.FinalizarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.IniciarSesionJuegoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerPreguntasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsFinalizarSesion
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsIniciarSesionJuego
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerPreguntas
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsResponderPregunta
import com.vialgo.app.dominio.casosdeuso.sesion.ResponderPreguntaUseCase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.ViewModelBase
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EstadoSesionJuego(
    val preguntas: List<Pregunta> = emptyList(),
    val indicePreguntaActual: Int = 0,
    val respuestas: List<RespuestaUsuario> = emptyList(),
    val tiempoRestanteMs: Long = 5000L,
    val mostrandoRetroalimentacion: Boolean = false,
    val opcionSeleccionada: String? = null,
    val sesionId: String = "",
    val xpAcumulado: Int = 0,
    val usuarioId: String = "",
)

sealed interface EventoSesion {
    data class IrAResultado(val sesionId: String) : EventoSesion
    data object Salir : EventoSesion
}

class SesionViewModel(
    private val iniciarSesionJuego: IniciarSesionJuegoUseCase,
    private val obtenerPreguntas: ObtenerPreguntasUseCase,
    private val responderPregunta: ResponderPreguntaUseCase,
    private val finalizarSesion: FinalizarSesionUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoSesionJuego>(EstadoSesionJuego()) {

    private val _eventos = MutableSharedFlow<EventoSesion>()
    val eventos: SharedFlow<EventoSesion> = _eventos.asSharedFlow()

    private var timerJob: Job? = null

    fun iniciar(usuarioId: String, leccionId: String, rol: String) {
        scope.launch {
            mostrarCargando()

            val sesionResult = iniciarSesionJuego.ejecutar(
                ParamsIniciarSesionJuego(
                    usuarioId = usuarioId,
                    leccionId = leccionId,
                    tipo = "normal",
                    rol = rol,
                )
            )

            if (sesionResult is Resultado.Error) {
                mostrarError(sesionResult.mensaje)
                return@launch
            }

            val sesion = (sesionResult as Resultado.Exito).datos

            val preguntasResult = obtenerPreguntas.ejecutar(ParamsObtenerPreguntas(leccionId))
            if (preguntasResult is Resultado.Error) {
                mostrarError(preguntasResult.mensaje)
                return@launch
            }

            val preguntas = (preguntasResult as Resultado.Exito).datos

            mostrarContenido(
                EstadoSesionJuego(
                    preguntas = preguntas,
                    sesionId = sesion.id,
                    usuarioId = usuarioId,
                )
            )

            iniciarTimer()
        }
    }

    fun seleccionarOpcion(opcionId: String) {
        val estadoActual = estadoActual() ?: return
        if (estadoActual.mostrandoRetroalimentacion) return
        if (estadoActual.opcionSeleccionada != null) return

        timerJob?.cancel()

        val preguntaActual = estadoActual.preguntas.getOrNull(estadoActual.indicePreguntaActual) ?: return
        val tiempoTranscurrido = 5000L - estadoActual.tiempoRestanteMs
        val tiempoRespuestaMs = minOf(tiempoTranscurrido, 5000L).toInt()
        val esCorrecta = preguntaActual.opciones.find { it.id == opcionId }?.esCorrecta ?: false

        actualizarEstado { it.copy(opcionSeleccionada = opcionId, mostrandoRetroalimentacion = true) }

        scope.launch {
            val resultado = responderPregunta.ejecutar(
                ParamsResponderPregunta(
                    sesionId = estadoActual.sesionId,
                    preguntaId = preguntaActual.id,
                    opcionId = opcionId,
                    fueCorrecta = esCorrecta,
                    tiempoRespuestaMs = tiempoRespuestaMs,
                    xpObtenido = 0,
                    usuarioId = estadoActual.usuarioId,
                )
            )

            val xpObs = if (resultado is Resultado.Exito) resultado.datos.xpObtenido else 0

            actualizarEstado { estado ->
                val nuevasRespuestas = if (resultado is Resultado.Exito) {
                    estado.respuestas + resultado.datos
                } else {
                    estado.respuestas
                }
                estado.copy(
                    respuestas = nuevasRespuestas,
                    xpAcumulado = estado.xpAcumulado + xpObs,
                )
            }

            delay(1500L)
            avanzarOFinalizar()
        }
    }

    fun salir() {
        timerJob?.cancel()
        scope.launch { _eventos.emit(EventoSesion.Salir) }
    }

    private fun estadoActual(): EstadoSesionJuego? {
        val ui = estadoUi.value
        return (ui as? com.vialgo.app.dominio.comun.EstadoUi.Contenido)?.datos
    }

    private fun iniciarTimer() {
        timerJob?.cancel()
        actualizarEstado { it.copy(tiempoRestanteMs = 5000L) }
        timerJob = scope.launch {
            while (true) {
                delay(100L)
                val estado = estadoActual() ?: break
                if (estado.mostrandoRetroalimentacion) break
                val nuevoTiempo = estado.tiempoRestanteMs - 100L
                if (nuevoTiempo <= 0L) {
                    actualizarEstado { it.copy(tiempoRestanteMs = 0L) }
                    autoSubmitIncorrecto()
                    break
                } else {
                    actualizarEstado { it.copy(tiempoRestanteMs = nuevoTiempo) }
                }
            }
        }
    }

    private fun autoSubmitIncorrecto() {
        val estado = estadoActual() ?: return
        val preguntaActual = estado.preguntas.getOrNull(estado.indicePreguntaActual) ?: return

        actualizarEstado { it.copy(mostrandoRetroalimentacion = true) }

        scope.launch {
            responderPregunta.ejecutar(
                ParamsResponderPregunta(
                    sesionId = estado.sesionId,
                    preguntaId = preguntaActual.id,
                    opcionId = "",
                    fueCorrecta = false,
                    tiempoRespuestaMs = 5000,
                    xpObtenido = 0,
                    usuarioId = estado.usuarioId,
                )
            )

            delay(1500L)
            avanzarOFinalizar()
        }
    }

    private fun avanzarOFinalizar() {
        val estado = estadoActual() ?: return
        val siguienteIndice = estado.indicePreguntaActual + 1

        if (siguienteIndice >= estado.preguntas.size) {
            finalizarSesionInterna(estado)
        } else {
            actualizarEstado {
                it.copy(
                    indicePreguntaActual = siguienteIndice,
                    opcionSeleccionada = null,
                    mostrandoRetroalimentacion = false,
                    tiempoRestanteMs = 5000L,
                )
            }
            iniciarTimer()
        }
    }

    private fun finalizarSesionInterna(estado: EstadoSesionJuego) {
        scope.launch {
            finalizarSesion.ejecutar(
                ParamsFinalizarSesion(
                    sesionId = estado.sesionId,
                    xpGanado = estado.xpAcumulado,
                )
            )
            _eventos.emit(EventoSesion.IrAResultado(estado.sesionId))
        }
    }
}
