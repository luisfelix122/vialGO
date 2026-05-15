package com.vialgo.app.presentacion.sesion

import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.presentacion.aprender.AprenderViewModel
import com.vialgo.app.presentacion.aprender.EstadoAprender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AprenderViewModelTest {

    private fun buildViewModel(
        repoContenido: FakeRepoContenidoAndroid = FakeRepoContenidoAndroid(),
        repoGami: FakeRepoGamificacionAndroid = FakeRepoGamificacionAndroid(),
        scheduler: kotlinx.coroutines.test.TestCoroutineScheduler? = null,
    ): AprenderViewModel {
        val scope = CoroutineScope(
            if (scheduler != null) UnconfinedTestDispatcher(scheduler) else UnconfinedTestDispatcher()
        )
        return AprenderViewModel(
            obtenerModulos = fakeObtenerModulosUseCase(repoContenido),
            obtenerProgreso = fakeObtenerProgresoUseCase(repoContenido),
            obtenerVidas = fakeObtenerVidasUseCase(repoGami),
            scope = scope,
        )
    }

    @Test
    fun `cargar contenido actualiza estado con modulos`() = runTest {
        val modulos = listOf(
            Modulo(
                id = "modulo-1",
                titulo = "Señales de transito",
                descripcion = "Aprende las señales",
                orden = 1,
                urlImagenPortada = null,
                rolesDisponibles = listOf(RolUsuario.CONDUCTOR),
                lecciones = emptyList(),
            )
        )
        val repoContenido = FakeRepoContenidoAndroid(
            resultadoObtenerModulos = Resultado.Exito(modulos)
        )
        val vm = buildViewModel(repoContenido = repoContenido, scheduler = testScheduler)

        vm.cargarContenido(usuarioId = "usuario-123", rol = RolUsuario.CONDUCTOR)

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoAprender>>(estado)
        assertEquals(1, estado.datos.modulos.size)
        assertEquals("Señales de transito", estado.datos.modulos[0].titulo)
    }

    @Test
    fun `cargar contenido con 0 vidas refleja vidas en estado`() = runTest {
        val repoGami = FakeRepoGamificacionAndroid(
            resultadoObtenerVidas = Resultado.Exito(vidaTestAndroid(cantidad = 0))
        )
        val vm = buildViewModel(repoGami = repoGami, scheduler = testScheduler)

        vm.cargarContenido("usuario-123", RolUsuario.CONDUCTOR)

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoAprender>>(estado)
        assertEquals(0, estado.datos.vidasRestantes)
    }

    @Test
    fun `error en modulos muestra estado con error`() = runTest {
        val repoContenido = FakeRepoContenidoAndroid(
            resultadoObtenerModulos = Resultado.Error("sin conexion")
        )
        val vm = buildViewModel(repoContenido = repoContenido, scheduler = testScheduler)

        vm.cargarContenido("usuario-123", RolUsuario.PEATONAL)

        val estado = vm.estadoUi.value
        assertIs<EstadoUi.Contenido<EstadoAprender>>(estado)
        assertEquals("sin conexion", estado.datos.errorGeneral)
    }
}
