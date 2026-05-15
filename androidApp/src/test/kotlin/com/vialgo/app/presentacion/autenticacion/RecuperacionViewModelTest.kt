package com.vialgo.app.presentacion.autenticacion

import app.cash.turbine.test
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RecuperacionViewModelTest {

    private fun vmConDatosCompletos(
        scope: CoroutineScope,
        resultado: Resultado<Unit> = Resultado.Exito(Unit),
    ): RecuperacionViewModel {
        val vm = RecuperacionViewModel(
            recuperarContrasena = fakeRecuperarContrasenaUseCase(resultado),
            scope = scope,
        )
        vm.onDniCambiado("12345678")
        vm.onRespuestaSeguridadCambiada("Firulais")
        vm.onNuevaContrasenaCambiada("NuevaPass1!")
        return vm
    }

    @Test
    fun `recuperacion exitosa marca exito y emite Volver`() = runTest {
        val vm = vmConDatosCompletos(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.eventos.test {
            vm.onRecuperar()

            val evento = awaitItem()
            assertIs<EventoNavegacion.Volver>(evento)
            cancelAndIgnoreRemainingEvents()
        }

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRecuperacion>>(estadoFinal)
        assertTrue(estadoFinal.datos.exito)
    }

    @Test
    fun `respuesta incorrecta muestra error`() = runTest {
        val mensajeError = "Respuesta de seguridad incorrecta"
        val vm = vmConDatosCompletos(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
            resultado = Resultado.Error(mensajeError),
        )

        vm.onRecuperar()

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRecuperacion>>(estadoFinal)
        assertEquals(mensajeError, estadoFinal.datos.errorGeneral)
    }

    @Test
    fun `estado inicial no tiene exito`() = runTest {
        val vm = RecuperacionViewModel(
            recuperarContrasena = fakeRecuperarContrasenaUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRecuperacion>>(estado)
        assertEquals(false, estado.datos.exito)
    }
}
