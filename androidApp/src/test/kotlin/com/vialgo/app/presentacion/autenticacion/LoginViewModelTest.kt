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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginViewModelTest {

    @Test
    fun `login exitoso navega a principal cuando tutorial completado`() = runTest {
        val useCase = fakeIniciarSesionUseCase(
            resultado = Resultado.Exito(usuarioPrueba(tutorialCompletado = true))
        )
        val vm = LoginViewModel(
            iniciarSesion = useCase,
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onDniCambiado("12345678")
        vm.onContrasenaCambiada("Password1!")

        vm.eventos.test {
            vm.onIniciarSesion()
            val evento = awaitItem()
            assertIs<EventoNavegacion.IrAPrincipal>(evento)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login exitoso con tutorial pendiente navega a onboarding`() = runTest {
        val useCase = fakeIniciarSesionUseCase(
            resultado = Resultado.Exito(usuarioPrueba(tutorialCompletado = false))
        )
        val vm = LoginViewModel(
            iniciarSesion = useCase,
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onDniCambiado("12345678")
        vm.onContrasenaCambiada("Password1!")

        vm.eventos.test {
            vm.onIniciarSesion()
            val evento = awaitItem()
            assertIs<EventoNavegacion.IrAOnboarding>(evento)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `credenciales invalidas muestra error y retiene DNI`() = runTest {
        val mensajeError = "DNI o contraseña incorrectos"
        val useCase = fakeIniciarSesionUseCase(
            resultado = Resultado.Error(mensajeError)
        )
        val vm = LoginViewModel(
            iniciarSesion = useCase,
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onDniCambiado("12345678")
        vm.onContrasenaCambiada("Password1!")
        vm.onIniciarSesion()

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoLogin>>(estadoFinal)
        assertEquals(mensajeError, estadoFinal.datos.errorGeneral)
        assertEquals("12345678", estadoFinal.datos.dni)
    }

    @Test
    fun `actualizar dni limpia error de dni`() = runTest {
        val vm = LoginViewModel(
            iniciarSesion = fakeIniciarSesionUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onDniCambiado("99999999")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoLogin>>(estado)
        assertEquals("99999999", estado.datos.dni)
        assertNull(estado.datos.errorDni)
    }

    @Test
    fun `actualizar contrasena limpia error de contrasena`() = runTest {
        val vm = LoginViewModel(
            iniciarSesion = fakeIniciarSesionUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.onContrasenaCambiada("nuevaPass")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoLogin>>(estado)
        assertEquals("nuevaPass", estado.datos.contrasena)
        assertNull(estado.datos.errorContrasena)
    }
}
