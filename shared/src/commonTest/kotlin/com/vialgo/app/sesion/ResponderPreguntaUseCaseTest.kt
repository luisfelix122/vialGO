package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.ParamsResponderPregunta
import com.vialgo.app.dominio.casosdeuso.sesion.ResponderPreguntaUseCase
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResponderPreguntaUseCaseTest {

    private val repoSesion = FakeRepositorioSesion()
    private val repoGamificacion = FakeRepositorioGamificacion()
    private val casoDeUso = ResponderPreguntaUseCase(repoSesion, repoGamificacion)

    private val paramsBase = ParamsResponderPregunta(
        sesionId = "sesion-123",
        preguntaId = "pregunta-1",
        opcionId = "opcion-1",
        fueCorrecta = true,
        tiempoRespuestaMs = 1000,
        xpObtenido = 50,
        usuarioId = "usuario-123",
    )

    @Test
    fun `respuesta correcta no consume vida`() = runTest {
        repoSesion.resultadoRegistrarRespuesta = Resultado.Exito(respuestaPrueba(fueCorrecta = true))

        val resultado = casoDeUso.ejecutar(paramsBase.copy(fueCorrecta = true))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(0, repoGamificacion.llamadasConsumirVida)
    }

    @Test
    fun `respuesta incorrecta consume una vida`() = runTest {
        repoSesion.resultadoRegistrarRespuesta = Resultado.Exito(respuestaPrueba(fueCorrecta = false))

        val resultado = casoDeUso.ejecutar(paramsBase.copy(fueCorrecta = false))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, repoGamificacion.llamadasConsumirVida)
    }

    @Test
    fun `no consume vida si el registro de respuesta falla`() = runTest {
        repoSesion.resultadoRegistrarRespuesta = Resultado.Error("error de red")

        val resultado = casoDeUso.ejecutar(paramsBase.copy(fueCorrecta = false))

        assertIs<Resultado.Error>(resultado)
        assertEquals(0, repoGamificacion.llamadasConsumirVida)
    }

    @Test
    fun `registra la respuesta en el repositorio`() = runTest {
        casoDeUso.ejecutar(paramsBase)

        assertEquals(1, repoSesion.llamadasRegistrarRespuesta)
    }
}
