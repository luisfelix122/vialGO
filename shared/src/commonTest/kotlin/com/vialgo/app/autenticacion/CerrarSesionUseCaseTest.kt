package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.CerrarSesionUseCase
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CerrarSesionUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = CerrarSesionUseCase(repositorio)

    @Test
    fun `cerrar sesion exitoso retorna Exito Unit`() = runTest {
        repositorio.resultadoCerrarSesion = Resultado.Exito(Unit)

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Exito<Unit>>(resultado)
        assertEquals(1, repositorio.llamadasCerrarSesion)
    }

    @Test
    fun `error al cerrar sesion se propaga`() = runTest {
        repositorio.resultadoCerrarSesion = Resultado.Error("Error de red al cerrar sesión")

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Error>(resultado)
        assertEquals("Error de red al cerrar sesión", resultado.mensaje)
    }
}
