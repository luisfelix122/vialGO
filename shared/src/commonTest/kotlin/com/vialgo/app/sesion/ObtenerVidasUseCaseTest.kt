package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerVidasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerVidas
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObtenerVidasUseCaseTest {

    private val repositorio = FakeRepositorioGamificacion()
    private val casoDeUso = ObtenerVidasUseCase(repositorio)

    @Test
    fun `delega al repositorio y retorna vidas del usuario`() = runTest {
        val vidaEsperada = vidaPrueba(cantidad = 5)
        repositorio.resultadoObtenerVidas = Resultado.Exito(vidaEsperada)

        val resultado = casoDeUso.ejecutar(ParamsObtenerVidas(usuarioId = "usuario-123"))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(5, (resultado as Resultado.Exito).datos.cantidad)
        assertEquals(1, repositorio.llamadasObtenerVidas)
    }

    @Test
    fun `retorna cero vidas cuando el usuario las agoto`() = runTest {
        repositorio.resultadoObtenerVidas = Resultado.Exito(vidaPrueba(cantidad = 0))

        val resultado = casoDeUso.ejecutar(ParamsObtenerVidas(usuarioId = "usuario-123"))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(0, (resultado as Resultado.Exito).datos.cantidad)
        assertEquals(1, repositorio.llamadasObtenerVidas)
    }

    @Test
    fun `propaga error del repositorio`() = runTest {
        repositorio.resultadoObtenerVidas = Resultado.Error("error al obtener vidas")

        val resultado = casoDeUso.ejecutar(ParamsObtenerVidas(usuarioId = "usuario-123"))

        assertIs<Resultado.Error>(resultado)
        assertEquals("error al obtener vidas", resultado.mensaje)
        assertEquals(1, repositorio.llamadasObtenerVidas)
    }
}
