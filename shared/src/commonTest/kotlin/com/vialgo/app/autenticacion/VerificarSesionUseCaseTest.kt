package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.VerificarSesionUseCase
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class VerificarSesionUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = VerificarSesionUseCase(repositorio)

    @Test
    fun `verificar sesion retorna usuario cuando hay sesion activa`() = runTest {
        val usuarioEsperado = FakeRepositorioAutenticacion.usuarioPrueba()
        repositorio.resultadoObtenerUsuario = Resultado.Exito(usuarioEsperado)

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(usuarioEsperado, (resultado as Resultado.Exito).datos)
        assertEquals(1, repositorio.llamadasObtenerUsuario)
    }

    @Test
    fun `verificar sesion retorna null cuando no hay sesion activa`() = runTest {
        repositorio.resultadoObtenerUsuario = Resultado.Exito(null)

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Exito<*>>(resultado)
        assertNull((resultado as Resultado.Exito).datos)
        assertEquals(1, repositorio.llamadasObtenerUsuario)
    }

    @Test
    fun `verificar sesion propaga error del repositorio`() = runTest {
        repositorio.resultadoObtenerUsuario = Resultado.Error("Error al verificar la sesion")

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Error>(resultado)
        assertEquals("Error al verificar la sesion", resultado.mensaje)
        assertEquals(1, repositorio.llamadasObtenerUsuario)
    }
}
