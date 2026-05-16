package com.vialgo.app.mapeadores

import com.vialgo.app.datos.mapeadores.ErrorMapeador
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ErrorMapeadorTest {

    private val mensajeGenerico = "Ocurrió un error inesperado. Intentá de nuevo más tarde."

    @Test
    fun `ErrorMapeador traduce SESSION_IMPORT_FAILED a mensaje de sesion`() {
        val resultado = ErrorMapeador.traducir("SESSION_IMPORT_FAILED")
        assertNotEquals(mensajeGenerico, resultado)
        assertTrue(resultado.contains("sesión"), "El mensaje debería mencionar 'sesión': $resultado")
    }

    @Test
    fun `ErrorMapeador traduce RETRY_EXHAUSTED a mensaje de reintento agotado`() {
        val resultado = ErrorMapeador.traducir("RETRY_EXHAUSTED")
        assertNotEquals(mensajeGenerico, resultado)
        assertTrue(resultado.isNotBlank())
    }

    @Test
    fun `ErrorMapeador traduce USER_PROFILE_NOT_FOUND a mensaje de perfil no encontrado`() {
        val resultado = ErrorMapeador.traducir("USER_PROFILE_NOT_FOUND")
        assertNotEquals(mensajeGenerico, resultado)
        assertTrue(resultado.contains("perfil"), "El mensaje debería mencionar 'perfil': $resultado")
    }

    @Test
    fun `ErrorMapeador traduce SESSION_IMPORT_FAILED en minusculas`() {
        val resultado = ErrorMapeador.traducir("session_import_failed")
        assertNotEquals(mensajeGenerico, resultado)
    }

    @Test
    fun `ErrorMapeador devuelve mensaje generico para error desconocido`() {
        val resultado = ErrorMapeador.traducir("CODIGO_INEXISTENTE_XYZ")
        assertEquals(mensajeGenerico, resultado)
    }

    @Test
    fun `ErrorMapeador devuelve mensaje generico para null`() {
        val resultado = ErrorMapeador.traducir(null)
        assertEquals(mensajeGenerico, resultado)
    }

    @Test
    fun `ErrorMapeador traduce SIGNIN_ERROR a mensaje existente`() {
        val resultado = ErrorMapeador.traducir("SIGNIN_ERROR")
        assertNotEquals(mensajeGenerico, resultado)
    }

    private fun assertEquals(expected: String, actual: String) {
        kotlin.test.assertEquals(expected, actual)
    }
}
