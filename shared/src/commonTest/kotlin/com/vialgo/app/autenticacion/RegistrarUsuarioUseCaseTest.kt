package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRegistro
import com.vialgo.app.dominio.casosdeuso.autenticacion.RegistrarUsuarioUseCase
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RegistrarUsuarioUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = RegistrarUsuarioUseCase(repositorio)

    private fun paramsValidos() = ParamsRegistro(
        dni = "12345678",
        contrasena = "secreto123",
        nombre = "Juan Perez",
        preguntaSeguridad = "¿Nombre de mascota?",
        respuestaSeguridad = "Firulais",
        rolActivo = "conductor",
        compromisoMinutos = 30,
    )

    @Test
    fun `registro exitoso retorna Usuario`() = runTest {
        val usuarioEsperado = FakeRepositorioAutenticacion.usuarioPrueba()
        repositorio.resultadoRegistrar = Resultado.Exito(usuarioEsperado)

        val resultado = casoDeUso.ejecutar(paramsValidos())

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(usuarioEsperado, (resultado as Resultado.Exito).datos)
        assertEquals(1, repositorio.llamadasRegistrar)
    }

    @Test
    fun `registro con DNI ya existente retorna Error del repositorio`() = runTest {
        repositorio.resultadoRegistrar = Resultado.Error("DNI_ALREADY_EXISTS")

        val resultado = casoDeUso.ejecutar(paramsValidos())

        assertIs<Resultado.Error>(resultado)
        assertEquals("DNI_ALREADY_EXISTS", resultado.mensaje)
    }

    @Test
    fun `registro con DNI invalido no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(dni = "ABCD1234")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRegistrar)
    }

    @Test
    fun `registro con contrasena corta no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(contrasena = "abc")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRegistrar)
    }

    @Test
    fun `registro con nombre vacio no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(nombre = "")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRegistrar)
    }

    @Test
    fun `registro con pregunta de seguridad vacia no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(preguntaSeguridad = "")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRegistrar)
    }

    @Test
    fun `registro con respuesta vacia no llama al repositorio`() = runTest {
        val params = paramsValidos().copy(respuestaSeguridad = "")

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasRegistrar)
    }
}
