package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerPreguntasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerPreguntas
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObtenerPreguntasUseCaseTest {

    private val repositorio = FakeRepositorioContenido()
    private val casoDeUso = ObtenerPreguntasUseCase(repositorio)

    @Test
    fun `delega al repositorio y retorna lista de preguntas`() = runTest {
        repositorio.resultadoObtenerPreguntas = Resultado.Exito(preguntasPrueba(3))

        val resultado = casoDeUso.ejecutar(ParamsObtenerPreguntas(leccionId = "leccion-abc"))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(3, (resultado as Resultado.Exito).datos.size)
        assertEquals(1, repositorio.llamadasObtenerPreguntas)
    }

    @Test
    fun `retorna lista vacia cuando no hay preguntas`() = runTest {
        repositorio.resultadoObtenerPreguntas = Resultado.Exito(emptyList())

        val resultado = casoDeUso.ejecutar(ParamsObtenerPreguntas(leccionId = "leccion-sin-preguntas"))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(0, (resultado as Resultado.Exito).datos.size)
        assertEquals(1, repositorio.llamadasObtenerPreguntas)
    }

    @Test
    fun `propaga error del repositorio`() = runTest {
        repositorio.resultadoObtenerPreguntas = Resultado.Error("sin conexion")

        val resultado = casoDeUso.ejecutar(ParamsObtenerPreguntas(leccionId = "leccion-error"))

        assertIs<Resultado.Error>(resultado)
        assertEquals("sin conexion", resultado.mensaje)
        assertEquals(1, repositorio.llamadasObtenerPreguntas)
    }
}
