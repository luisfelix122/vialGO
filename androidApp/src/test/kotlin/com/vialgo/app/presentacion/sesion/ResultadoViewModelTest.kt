package com.vialgo.app.presentacion.sesion

import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResultadoViewModelTest {

    private fun buildViewModel(
        repoSesion: FakeRepoSesionAndroid = FakeRepoSesionAndroid(),
        scheduler: kotlinx.coroutines.test.TestCoroutineScheduler? = null,
    ): ResultadoViewModel {
        val scope = CoroutineScope(
            if (scheduler != null) UnconfinedTestDispatcher(scheduler) else UnconfinedTestDispatcher()
        )
        return ResultadoViewModel(repositorioSesion = repoSesion, scope = scope)
    }

    @Test
    fun `cargar expone xp ganado desde la sesion`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoObtenerSesion = Resultado.Exito(sesionTestAndroid(xpGanado = 250))
        )
        val vm = buildViewModel(repoSesion = repoSesion, scheduler = testScheduler)

        vm.cargar("sesion-123")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoResultado>>(estado)
        assertEquals(250, estado.datos.xpGanado)
    }

    @Test
    fun `cargar expone total de preguntas`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoObtenerSesion = Resultado.Exito(sesionTestAndroid())
        )
        val vm = buildViewModel(repoSesion = repoSesion, scheduler = testScheduler)

        vm.cargar("sesion-123")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoResultado>>(estado)
        assertEquals(5, estado.datos.totalPreguntas)
    }

    @Test
    fun `error al cargar muestra estado de error`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoObtenerSesion = Resultado.Error("sesion no encontrada")
        )
        val vm = buildViewModel(repoSesion = repoSesion, scheduler = testScheduler)

        vm.cargar("sesion-inexistente")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Error>(estado)
    }
}
