package com.vialgo.app.presentacion.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsLogin
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

data class EstadoLogin(
    val dni: String = "",
    val contrasena: String = "",
    val errorDni: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
)

class LoginViewModel(
    private val iniciarSesion: IniciarSesionUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoLogin>(EstadoLogin()) {

    private val _eventos = MutableSharedFlow<EventoNavegacion>()
    val eventos: SharedFlow<EventoNavegacion> = _eventos.asSharedFlow()

    fun onDniCambiado(valor: String) {
        actualizarEstado { it.copy(dni = valor, errorDni = null) }
    }

    fun onContrasenaCambiada(valor: String) {
        actualizarEstado { it.copy(contrasena = valor, errorContrasena = null) }
    }

    fun onIniciarSesion() {
        val estadoActual = (estadoUi.value as? EstadoUi.Contenido)?.datos ?: return

        scope.launch {
            mostrarCargando()

            val resultado = iniciarSesion.ejecutar(
                ParamsLogin(
                    dni = estadoActual.dni,
                    contrasena = estadoActual.contrasena,
                )
            )

            when (resultado) {
                is Resultado.Exito -> {
                    mostrarContenido(EstadoLogin(dni = estadoActual.dni))
                    val evento = if (resultado.datos.tutorialCompletado) {
                        EventoNavegacion.IrAPrincipal
                    } else {
                        EventoNavegacion.IrAOnboarding
                    }
                    _eventos.emit(evento)
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
