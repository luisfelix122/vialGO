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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InvitadoViewModelTest {

    @Test
    fun `inicio como invitado navega a principal`() = runTest {
        val vm = InvitadoViewModel(
            iniciarSesionInvitado = fakeIniciarSesionInvitadoUseCase(
                resultado = Resultado.Exito(usuarioInvitadoPrueba())
            ),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.eventos.test {
            vm.onIniciarComoInvitado()
            val evento = awaitItem()
            assertIs<EventoNavegacion.IrAPrincipal>(evento)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `inicio como invitado con error muestra error`() = runTest {
        val mensajeError = "Sin conexion a internet"
        val vm = InvitadoViewModel(
            iniciarSesionInvitado = fakeIniciarSesionInvitadoUseCase(
                resultado = Resultado.Error(mensajeError)
            ),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onIniciarComoInvitado()

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoInvitado>>(estadoFinal)
        assertEquals(mensajeError, estadoFinal.datos.errorGeneral)
    }

    @Test
    fun `contador incrementa con cada pregunta respondida`() = runTest {
        val vm = InvitadoViewModel(
            iniciarSesionInvitado = fakeIniciarSesionInvitadoUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        repeat(3) { vm.onPreguntaRespondida() }

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoInvitado>>(estado)
        assertEquals(3, estado.datos.contadorPreguntas)
        assertFalse(estado.datos.mostrarPromptRegistro)
    }

    @Test
    fun `mostrar prompt registro al llegar a 5 preguntas`() = runTest {
        val vm = InvitadoViewModel(
            iniciarSesionInvitado = fakeIniciarSesionInvitadoUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        repeat(5) { vm.onPreguntaRespondida() }

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoInvitado>>(estado)
        assertEquals(5, estado.datos.contadorPreguntas)
        assertTrue(estado.datos.mostrarPromptRegistro)
    }

    @Test
    fun `estado inicial no muestra prompt registro`() = runTest {
        val vm = InvitadoViewModel(
            iniciarSesionInvitado = fakeIniciarSesionInvitadoUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoInvitado>>(estado)
        assertEquals(0, estado.datos.contadorPreguntas)
        assertFalse(estado.datos.mostrarPromptRegistro)
    }
}
