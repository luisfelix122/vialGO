package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsLogin
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IniciarSesionUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = IniciarSesionUseCase(repositorio)

    @Test
    fun `login exitoso retorna Usuario`() = runTest {
        val usuarioEsperado = FakeRepositorioAutenticacion.usuarioPrueba()
        repositorio.resultadoIniciarSesion = Resultado.Exito(usuarioEsperado)

        val resultado = casoDeUso.ejecutar(ParamsLogin("12345678", "contrasena"))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(usuarioEsperado, (resultado as Resultado.Exito).datos)
        assertEquals(1, repositorio.llamadasIniciarSesion)
    }

    @Test
    fun `login con credenciales invalidas retorna Error del repositorio`() = runTest {
        repositorio.resultadoIniciarSesion = Resultado.Error("INVALID_CREDENTIALS")

        val resultado = casoDeUso.ejecutar(ParamsLogin("12345678", "mala"))

        assertIs<Resultado.Error>(resultado)
        assertEquals("INVALID_CREDENTIALS", resultado.mensaje)
    }

    @Test
    fun `login con DNI invalido no llama al repositorio`() = runTest {
        val resultado = casoDeUso.ejecutar(ParamsLogin("abc", "contrasena"))

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasIniciarSesion)
    }

    @Test
    fun `login con DNI de 7 digitos retorna Error de validacion`() = runTest {
        val resultado = casoDeUso.ejecutar(ParamsLogin("1234567", "contrasena"))

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasIniciarSesion)
    }

    @Test
    fun `login con contrasena vacia no llama al repositorio`() = runTest {
        val resultado = casoDeUso.ejecutar(ParamsLogin("12345678", ""))

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repositorio.llamadasIniciarSesion)
    }
}
