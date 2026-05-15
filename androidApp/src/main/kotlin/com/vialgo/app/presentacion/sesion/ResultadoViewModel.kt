package com.vialgo.app.presentacion.sesion

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.ViewModelBase
import com.vialgo.app.dominio.repositorios.RepositorioSesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EstadoResultado(
    val xpGanado: Int = 0,
    val respuestasCorrectas: Int = 0,
    val totalPreguntas: Int = 0,
)

sealed interface EventoResultado {
    data object IrAAprender : EventoResultado
}

class ResultadoViewModel(
    private val repositorioSesion: RepositorioSesion,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoResultado>(EstadoResultado()) {

    private val _eventos = MutableSharedFlow<EventoResultado>()
    val eventos: SharedFlow<EventoResultado> = _eventos.asSharedFlow()

    fun cargar(sesionId: String) {
        scope.launch {
            mostrarCargando()

            when (val resultado = repositorioSesion.obtenerSesion(sesionId)) {
                is Resultado.Exito -> {
                    val sesion = resultado.datos
                    mostrarContenido(
                        EstadoResultado(
                            xpGanado = sesion.xpGanado,
                            respuestasCorrectas = 0,
                            totalPreguntas = sesion.preguntasTotales,
                        )
                    )
                }
                is Resultado.Error -> mostrarError(resultado.mensaje)
                else -> Unit
            }
        }
    }

    fun continuar() {
        scope.launch { _eventos.emit(EventoResultado.IrAAprender) }
    }
}
