package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.IniciarSesionJuegoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsIniciarSesionJuego
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IniciarSesionJuegoUseCaseTest {

    private val repoSesion = FakeRepositorioSesion()
    private val repoGamificacion = FakeRepositorioGamificacion()
    private val casoDeUso = IniciarSesionJuegoUseCase(repoSesion, repoGamificacion)

    private val params = ParamsIniciarSesionJuego(
        usuarioId = "usuario-123",
        leccionId = "leccion-1",
        tipo = "normal",
        rol = "conductor",
    )

    @Test
    fun `bloquea inicio cuando el usuario tiene 0 vidas`() = runTest {
        repoGamificacion.resultadoObtenerVidas = Resultado.Exito(vidaPrueba(cantidad = 0))

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Error>(resultado)
        assertEquals("No tienes vidas disponibles", resultado.mensaje)
        assertEquals(0, repoSesion.llamadasIniciarSesion)
    }

    @Test
    fun `permite inicio cuando el usuario tiene vidas disponibles`() = runTest {
        repoGamificacion.resultadoObtenerVidas = Resultado.Exito(vidaPrueba(cantidad = 3))
        repoSesion.resultadoIniciarSesion = Resultado.Exito(sesionPrueba())

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, repoSesion.llamadasIniciarSesion)
    }

    @Test
    fun `permite inicio cuando el usuario tiene exactamente 1 vida`() = runTest {
        repoGamificacion.resultadoObtenerVidas = Resultado.Exito(vidaPrueba(cantidad = 1))

        val resultado = casoDeUso.ejecutar(params)

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, repoSesion.llamadasIniciarSesion)
    }

    @Test
    fun `continua si falla la consulta de vidas (fail-open)`() = runTest {
        repoGamificacion.resultadoObtenerVidas = Resultado.Error("error de red")

        val resultado = casoDeUso.ejecutar(params)

        // When vida query fails (not Exito), we don't block — fail-open
        assertIs<Resultado.Exito<*>>(resultado)
    }
}
