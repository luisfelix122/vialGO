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

class RegistroViewModelTest {

    private fun vmConDatosCompletos(
        scope: CoroutineScope,
        resultado: Resultado<com.vialgo.app.dominio.entidades.Usuario> = Resultado.Exito(
            usuarioPrueba()
        ),
    ): RegistroViewModel {
        val vm = RegistroViewModel(
            registrarUsuario = fakeRegistrarUsuarioUseCase(resultado),
            scope = scope,
        )
        vm.onDniCambiado("12345678")
        vm.onContrasenaCambiada("Password1!")
        vm.onNombreCambiado("Juan Perez")
        vm.onPreguntaSeguridadCambiada("¿Nombre de tu mascota?")
        vm.onRespuestaSeguridadCambiada("Firulais")
        return vm
    }

    @Test
    fun `registro exitoso navega a onboarding`() = runTest {
        val vm = vmConDatosCompletos(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        vm.eventos.test {
            vm.onRegistrar()
            val evento = awaitItem()
            assertIs<EventoNavegacion.IrAOnboarding>(evento)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dni ya existe muestra error`() = runTest {
        val mensajeError = "El DNI ya está registrado"
        val vm = vmConDatosCompletos(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
            resultado = Resultado.Error(mensajeError),
        )

        vm.onRegistrar()

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRegistro>>(estadoFinal)
        assertEquals(mensajeError, estadoFinal.datos.errorGeneral)
    }

    @Test
    fun `campos faltantes muestra error de validacion`() = runTest {
        // El use case real valida — DNI invalido genera error
        val vm = RegistroViewModel(
            registrarUsuario = fakeRegistrarUsuarioUseCase(
                resultado = Resultado.Error("El DNI debe tener exactamente 8 dígitos")
            ),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )
        // dni invalido
        vm.onDniCambiado("abc")

        vm.onRegistrar()

        val estadoFinal = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRegistro>>(estadoFinal)
        assertEquals("El DNI debe tener exactamente 8 dígitos", estadoFinal.datos.errorGeneral)
    }

    @Test
    fun `rol activo por defecto es conductor`() = runTest {
        val vm = RegistroViewModel(
            registrarUsuario = fakeRegistrarUsuarioUseCase(),
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        )

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoRegistro>>(estado)
        assertEquals("conductor", estado.datos.rolActivo)
    }
}
