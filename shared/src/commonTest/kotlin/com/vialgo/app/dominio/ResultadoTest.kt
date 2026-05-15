package com.vialgo.app.dominio

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.comun.alError
import com.vialgo.app.dominio.comun.alExito
import com.vialgo.app.dominio.comun.mapear
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResultadoTest {

    @Test
    fun `Exito contiene los datos correctos`() {
        val resultado = Resultado.Exito(42)
        assertIs<Resultado.Exito<Int>>(resultado)
        assertEquals(42, resultado.datos)
    }

    @Test
    fun `Error contiene mensaje y causa nula por defecto`() {
        val resultado = Resultado.Error("algo fallo")
        assertIs<Resultado.Error>(resultado)
        assertEquals("algo fallo", resultado.mensaje)
        assertNull(resultado.causa)
    }

    @Test
    fun `Error contiene causa cuando se provee`() {
        val excepcion = RuntimeException("boom")
        val resultado = Resultado.Error("error con causa", excepcion)
        assertEquals(excepcion, resultado.causa)
    }

    @Test
    fun `Cargando es singleton`() {
        val a = Resultado.Cargando
        val b = Resultado.Cargando
        assertTrue(a === b)
    }

    @Test
    fun `mapear transforma Exito correctamente`() {
        val resultado = Resultado.Exito(5)
        val mapeado = resultado.mapear { it * 2 }
        assertIs<Resultado.Exito<Int>>(mapeado)
        assertEquals(10, mapeado.datos)
    }

    @Test
    fun `mapear no altera Error`() {
        val resultado: Resultado<Int> = Resultado.Error("fallo")
        val mapeado = resultado.mapear { it * 2 }
        assertIs<Resultado.Error>(mapeado)
        assertEquals("fallo", mapeado.mensaje)
    }

    @Test
    fun `mapear no altera Cargando`() {
        val resultado: Resultado<Int> = Resultado.Cargando
        val mapeado = resultado.mapear { it * 2 }
        assertIs<Resultado.Cargando>(mapeado)
    }

    @Test
    fun `alExito ejecuta accion solo en Exito`() {
        var invocado = false
        Resultado.Exito("dato").alExito { invocado = true }
        assertTrue(invocado)
    }

    @Test
    fun `alExito no ejecuta accion en Error`() {
        var invocado = false
        val resultado: Resultado<String> = Resultado.Error("fallo")
        resultado.alExito { invocado = true }
        assertTrue(!invocado)
    }

    @Test
    fun `alError ejecuta accion solo en Error`() {
        var mensajeCapturado: String? = null
        Resultado.Error("error capturado").alError { msg, _ -> mensajeCapturado = msg }
        assertEquals("error capturado", mensajeCapturado)
    }
}
