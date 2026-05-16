package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerProgresoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerProgreso
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObtenerProgresoUseCaseTest {

    private val repositorio = FakeRepositorioContenido()
    private val casoDeUso = ObtenerProgresoUseCase(repositorio)

    private val parametrosPrueba = ParamsObtenerProgreso(
        usuarioId = "usuario-123",
        rol = "conductor",
    )

    @Test
    fun `delega al repositorio y retorna lista de progreso`() = runTest {
        val progresoEsperado = listOf(
            ProgresoLeccion(
                id = "prog-001",
                usuarioId = "usuario-123",
                leccionId = "leccion-456",
                rol = "conductor",
                completada = true,
                estrellas = 3,
                mejorXp = 200,
                completadaEn = Instant.parse("2024-01-01T10:00:00Z"),
                actualizadoEn = Instant.parse("2024-01-01T10:00:00Z"),
            ),
        )
        repositorio.resultadoObtenerProgreso = Resultado.Exito(progresoEsperado)

        val resultado = casoDeUso.ejecutar(parametrosPrueba)

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, (resultado as Resultado.Exito).datos.size)
        assertEquals(1, repositorio.llamadasObtenerProgreso)
    }

    @Test
    fun `retorna lista vacia cuando no hay progreso registrado`() = runTest {
        repositorio.resultadoObtenerProgreso = Resultado.Exito(emptyList())

        val resultado = casoDeUso.ejecutar(parametrosPrueba)

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(0, (resultado as Resultado.Exito).datos.size)
        assertEquals(1, repositorio.llamadasObtenerProgreso)
    }

    @Test
    fun `propaga error del repositorio`() = runTest {
        repositorio.resultadoObtenerProgreso = Resultado.Error("error al obtener progreso")

        val resultado = casoDeUso.ejecutar(parametrosPrueba)

        assertIs<Resultado.Error>(resultado)
        assertEquals("error al obtener progreso", resultado.mensaje)
        assertEquals(1, repositorio.llamadasObtenerProgreso)
    }
}
