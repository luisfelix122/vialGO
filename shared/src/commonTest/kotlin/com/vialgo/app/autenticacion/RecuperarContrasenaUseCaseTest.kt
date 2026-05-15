package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRecuperacion
import com.vialgo.app.dominio.casosdeuso.autenticacion.RecuperarContrasenaUseCase
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RecuperarContrasenaUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = RecuperarContrasenaUseCase(repositorio)

    private fun paramsValidos() = ParamsRecuperacion(
        dni = "12345678",
        respuestaSeguridad = "Firulais",
        nuevaContrasena = "nuevacontrasena123",
    )

    @Test
    fun `recuperacion exitosa retorna Exito Unit`() = runTest {
        repositorio.resultadoRecuperar = Resultado.Exito(Unit)

        val resultado = casoDeUso.ejecutar(paramsValidos())

        assertIs<Resultado.Exito<Unit>>(resultado)
        assertEquals(1, repositorio.llamadasRecuperar)
    }

    @Test
    fun `respuesta incorrecta retorna Error del repositorio`() = runTest {
        repositorio.resultadoRecuperar = Resultado.Error("WRONG_ANSWER")

        val resultado = casoDeUso.ejecutar(paramsValidos())

        assertIs<Resultado.Error>(resultado)
        assertEquals("WRONG_ANSWER", resultado.mensaje)
    }

    @Test
    fun `rate limited retorna Error del repositorio`() = runTest {
        repositorio.resultadoRecuperar = Resultado.Error("RATE_LIMITED")

        val resultado = casoDeUso.ejecutar(paramsValidos())

        assertIs<Resultado.Error>(resultado)
        assertEquals("RATE_LIMITED", resultado.mensaje)
    }

    @Test
    fun `DNI invalido no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(dni = "corto")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRecuperar)
    }

    @Test
    fun `nueva contrasena corta no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(nuevaContrasena = "abc")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRecuperar)
    }

    @Test
    fun `respuesta de seguridad vacia no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(respuestaSeguridad = "")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRecuperar)
    }
}
