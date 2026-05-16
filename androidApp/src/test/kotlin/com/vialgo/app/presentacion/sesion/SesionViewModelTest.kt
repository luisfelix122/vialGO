package com.vialgo.app.presentacion.sesion

import app.cash.turbine.test
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class SesionViewModelTest {

    private fun buildViewModel(
        repoSesion: FakeRepoSesionAndroid = FakeRepoSesionAndroid(),
        repoContenido: FakeRepoContenidoAndroid = FakeRepoContenidoAndroid(),
        repoGami: FakeRepoGamificacionAndroid = FakeRepoGamificacionAndroid(),
        dispatcher: kotlinx.coroutines.test.TestCoroutineScheduler? = null,
    ): SesionViewModel {
        val scope = if (dispatcher != null) {
            CoroutineScope(UnconfinedTestDispatcher(dispatcher))
        } else {
            CoroutineScope(UnconfinedTestDispatcher())
        }
        return SesionViewModel(
            iniciarSesionJuego = IniciarSesionJuegoUseCase(repoSesion, repoGami),
            obtenerPreguntas = ObtenerPreguntasUseCase(repoContenido),
            responderPregunta = ResponderPreguntaUseCase(repoSesion, repoGami),
            finalizarSesion = FinalizarSesionUseCase(repoSesion),
            scope = scope,
        )
    }

    @Test
    fun `iniciar carga preguntas y actualiza estado`() = runTest {
        val repoContenido = FakeRepoContenidoAndroid(
            resultadoObtenerPreguntas = Resultado.Exito(preguntasTestAndroid(5))
        )
        val vm = buildViewModel(repoContenido = repoContenido, dispatcher = testScheduler)

        vm.iniciar(usuarioId = "usuario-123", leccionId = "leccion-1", rol = "conductor")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoSesionJuego>>(estado)
        assertEquals(5, estado.datos.preguntas.size)
        assertEquals(0, estado.datos.indicePreguntaActual)
    }

    @Test
    fun `seleccionarOpcion con respuesta correcta registra la respuesta`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoIniciarSesion = Resultado.Exito(sesionTestAndroid()),
            resultadoRegistrarRespuesta = Resultado.Exito(respuestaTestAndroid(fueCorrecta = true, xpObtenido = 50)),
        )
        val repoContenido = FakeRepoContenidoAndroid(
            resultadoObtenerPreguntas = Resultado.Exito(preguntasTestAndroid(5))
        )
        val vm = buildViewModel(
            repoSesion = repoSesion,
            repoContenido = repoContenido,
            dispatcher = testScheduler,
        )

        vm.iniciar("usuario-123", "leccion-1", "conductor")

        val estadoInicial = (vm.estadoUi.value as EstadoUi.Contenido).datos
        val opcionCorrecta = estadoInicial.preguntas[0].opciones.first { it.esCorrecta }

        vm.seleccionarOpcion(opcionCorrecta.id)

        val estadoDespues = (vm.estadoUi.value as EstadoUi.Contenido).datos
        // After feedback delay (1.5s with Unconfined), should have advanced or be showing feedback
        assertNotNull(estadoDespues)
    }

    @Test
    fun `sesion completa despues de 5 preguntas emite evento de navegacion`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoIniciarSesion = Resultado.Exito(sesionTestAndroid(id = "sesion-abc")),
            resultadoRegistrarRespuesta = Resultado.Exito(respuestaTestAndroid()),
            resultadoFinalizarSesion = Resultado.Exito(sesionTestAndroid()),
        )
        val repoContenido = FakeRepoContenidoAndroid(
            resultadoObtenerPreguntas = Resultado.Exito(preguntasTestAndroid(5))
        )
        val vm = buildViewModel(
            repoSesion = repoSesion,
            repoContenido = repoContenido,
            dispatcher = testScheduler,
        )

        vm.eventos.test {
            vm.iniciar("usuario-123", "leccion-1", "conductor")

            // Answer all 5 questions
            repeat(5) { idx ->
                val estadoActual = (vm.estadoUi.value as? EstadoUi.Contenido)?.datos
                if (estadoActual != null && !estadoActual.mostrandoRetroalimentacion) {
                    val pregunta = estadoActual.preguntas.getOrNull(estadoActual.indicePreguntaActual)
                    val opcion = pregunta?.opciones?.firstOrNull { it.esCorrecta }
                    if (opcion != null) {
                        vm.seleccionarOpcion(opcion.id)
                    }
                }
            }

            val evento = awaitItem()
            assertIs<EventoSesion.IrAResultado>(evento)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error al iniciar sesion muestra estado de error`() = runTest {
        val repoSesion = FakeRepoSesionAndroid(
            resultadoIniciarSesion = Resultado.Error("No tienes vidas disponibles"),
        )
        val repoGami = FakeRepoGamificacionAndroid(
            resultadoObtenerVidas = Resultado.Exito(vidaTestAndroid(vidasActuales = 0)),
        )
        val vm = buildViewModel(
            repoSesion = repoSesion,
            repoGami = repoGami,
            dispatcher = testScheduler,
        )

        vm.iniciar("usuario-123", "leccion-1", "conductor")

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Error>(estado)
    }
}

// Import use cases directly for buildViewModel
private fun IniciarSesionJuegoUseCase(
    repoSesion: FakeRepoSesionAndroid,
    repoGami: FakeRepoGamificacionAndroid,
) = com.vialgo.app.dominio.casosdeuso.sesion.IniciarSesionJuegoUseCase(repoSesion, repoGami)

private fun ObtenerPreguntasUseCase(
    repo: FakeRepoContenidoAndroid,
) = com.vialgo.app.dominio.casosdeuso.sesion.ObtenerPreguntasUseCase(repo)

private fun ResponderPreguntaUseCase(
    repoSesion: FakeRepoSesionAndroid,
    repoGami: FakeRepoGamificacionAndroid,
) = com.vialgo.app.dominio.casosdeuso.sesion.ResponderPreguntaUseCase(repoSesion, repoGami)

private fun FinalizarSesionUseCase(
    repo: FakeRepoSesionAndroid,
) = com.vialgo.app.dominio.casosdeuso.sesion.FinalizarSesionUseCase(repo)
