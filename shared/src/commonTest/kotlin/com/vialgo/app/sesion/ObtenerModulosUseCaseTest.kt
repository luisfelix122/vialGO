package com.vialgo.app.sesion

import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerModulosUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerModulos
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RolUsuario
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObtenerModulosUseCaseTest {

    private val repositorio = FakeRepositorioContenido()
    private val casoDeUso = ObtenerModulosUseCase(repositorio)

    @Test
    fun `delega al repositorio con el rol correcto`() = runTest {
        val resultado = casoDeUso.ejecutar(ParamsObtenerModulos(RolUsuario.CONDUCTOR))

        assertIs<Resultado.Exito<*>>(resultado)
        assertEquals(1, repositorio.llamadasObtenerModulos)
    }

    @Test
    fun `propaga error del repositorio`() = runTest {
        repositorio.resultadoObtenerModulos = Resultado.Error("sin conexion")

        val resultado = casoDeUso.ejecutar(ParamsObtenerModulos(RolUsuario.PEATONAL))

        assertIs<Resultado.Error>(resultado)
        assertEquals("sin conexion", resultado.mensaje)
    }
}
