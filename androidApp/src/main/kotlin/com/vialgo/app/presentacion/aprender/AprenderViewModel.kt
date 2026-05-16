package com.vialgo.app.presentacion.aprender

import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerModulosUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerProgresoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerVidasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerModulos
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerProgreso
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerVidas
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.ViewModelBase
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EstadoAprender(
    val modulos: List<Modulo> = emptyList(),
    val progresos: Map<String, ProgresoLeccion> = emptyMap(),
    val vidasRestantes: Int = 5,
    val errorGeneral: String? = null,
)

sealed interface EventoAprender {
    data class IrASesion(val leccionId: String) : EventoAprender
}

class AprenderViewModel(
    private val obtenerModulos: ObtenerModulosUseCase,
    private val obtenerProgreso: ObtenerProgresoUseCase,
    private val obtenerVidas: ObtenerVidasUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) : ViewModelBase<EstadoAprender>(EstadoAprender()) {

    private val _eventos = MutableSharedFlow<EventoAprender>()
    val eventos: SharedFlow<EventoAprender> = _eventos.asSharedFlow()

    fun cargarContenido(usuarioId: String, rol: RolUsuario) {
        scope.launch {
            mostrarCargando()

            val modulosResult = obtenerModulos.ejecutar(ParamsObtenerModulos(rol))
            val progresoResult = obtenerProgreso.ejecutar(
                ParamsObtenerProgreso(usuarioId = usuarioId, rol = rol.name.lowercase())
            )
            val vidasResult = obtenerVidas.ejecutar(ParamsObtenerVidas(usuarioId))

            val modulos = when (modulosResult) {
                is Resultado.Exito -> modulosResult.datos
                is Resultado.Error -> {
                    mostrarContenido(EstadoAprender(errorGeneral = modulosResult.mensaje))
                    return@launch
                }
                else -> emptyList()
            }

            val progresos = when (progresoResult) {
                is Resultado.Exito -> progresoResult.datos.associateBy { it.leccionId }
                else -> emptyMap()
            }

            val vidas = when (vidasResult) {
                is Resultado.Exito -> vidasResult.datos.vidasActuales
                else -> 5
            }

            mostrarContenido(
                EstadoAprender(
                    modulos = modulos,
                    progresos = progresos,
                    vidasRestantes = vidas,
                )
            )
        }
    }

    fun navegarASesion(leccionId: String) {
        scope.launch {
            _eventos.emit(EventoAprender.IrASesion(leccionId))
        }
    }
}
