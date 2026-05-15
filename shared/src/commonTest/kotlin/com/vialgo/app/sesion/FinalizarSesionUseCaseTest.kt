package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.FinalizarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsFinalizarSesion
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FinalizarSesionUseCaseTest {

    private val repoSesion = FakeRepositorioSesion()
    private val casoDeUso = FinalizarSesionUseCase(repoSesion)

    @Test
    fun `delega a repositorio con los parametros correctos`() = runTest {
        repoSesion.resultadoFinalizarSesion = Resultado.Exito(sesionPrueba(xpGanado = 250))

        val resultado = casoDeUso.ejecutar(ParamsFinalizarSesion(sesionId = "sesion-123", xpGanado = 250))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, repoSesion.llamadasFinalizarSesion)
    }

    @Test
    fun `propaga error del repositorio`() = runTest {
        repoSesion.resultadoFinalizarSesion = Resultado.Error("sesion no encontrada")

        val resultado = casoDeUso.ejecutar(ParamsFinalizarSesion(sesionId = "sesion-inexistente", xpGanado = 0))

        assertIs<Resultado.Error>(resultado)
        assertEquals("sesion no encontrada", resultado.mensaje)
    }
}
