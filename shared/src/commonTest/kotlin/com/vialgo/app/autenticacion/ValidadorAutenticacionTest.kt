package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.validacion.ValidadorAutenticacion
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ValidadorAutenticacionTest {

    // --- validarDni ---

    @Test
    fun `validarDni con 8 digitos validos retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarDni("12345678")
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    @Test
    fun `validarDni con menos de 8 caracteres retorna Error`() {
        val resultado = ValidadorAutenticacion.validarDni("1234567")
        assertIs<Resultado.Error>(resultado)
        assertTrue(resultado.mensaje.contains("8"))
    }

    @Test
    fun `validarDni con mas de 8 caracteres retorna Error`() {
        val resultado = ValidadorAutenticacion.validarDni("123456789")
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarDni con letras retorna Error`() {
        val resultado = ValidadorAutenticacion.validarDni("1234567A")
        assertIs<Resultado.Error>(resultado)
        assertTrue(resultado.mensaje.contains("dígitos"))
    }

    @Test
    fun `validarDni con simbolos retorna Error`() {
        val resultado = ValidadorAutenticacion.validarDni("1234-678")
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarDni con cadena vacia retorna Error`() {
        val resultado = ValidadorAutenticacion.validarDni("")
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarDni con todos ceros retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarDni("00000000")
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    // --- validarContrasena ---

    @Test
    fun `validarContrasena no vacia en login retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarContrasena("a")
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    @Test
    fun `validarContrasena vacia retorna Error`() {
        val resultado = ValidadorAutenticacion.validarContrasena("")
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarContrasena con 6 chars en registro retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarContrasena("abc123", esRegistro = true)
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    @Test
    fun `validarContrasena con 5 chars en registro retorna Error`() {
        val resultado = ValidadorAutenticacion.validarContrasena("abc12", esRegistro = true)
        assertIs<Resultado.Error>(resultado)
        assertTrue(resultado.mensaje.contains("6"))
    }

    @Test
    fun `validarContrasena con 1 char en login retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarContrasena("x", esRegistro = false)
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    // --- validarRegistro ---

    @Test
    fun `validarRegistro con todos los campos validos retorna Exito`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "12345678",
            contrasena = "secreto123",
            nombre = "Juan Perez",
            preguntaSeguridad = "¿Nombre mascota?",
            respuestaSeguridad = "Firulais",
        )
        assertIs<Resultado.Exito<Unit>>(resultado)
    }

    @Test
    fun `validarRegistro con DNI invalido retorna Error`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "abc",
            contrasena = "secreto123",
            nombre = "Juan",
            preguntaSeguridad = "¿Pregunta?",
            respuestaSeguridad = "Respuesta",
        )
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarRegistro con contrasena corta retorna Error`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "12345678",
            contrasena = "abc",
            nombre = "Juan",
            preguntaSeguridad = "¿Pregunta?",
            respuestaSeguridad = "Respuesta",
        )
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarRegistro con nombre vacio retorna Error`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "12345678",
            contrasena = "secreto123",
            nombre = "   ",
            preguntaSeguridad = "¿Pregunta?",
            respuestaSeguridad = "Respuesta",
        )
        assertIs<Resultado.Error>(resultado)
        assertTrue(resultado.mensaje.contains("nombre"))
    }

    @Test
    fun `validarRegistro con pregunta vacia retorna Error`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "12345678",
            contrasena = "secreto123",
            nombre = "Juan",
            preguntaSeguridad = "",
            respuestaSeguridad = "Respuesta",
        )
        assertIs<Resultado.Error>(resultado)
    }

    @Test
    fun `validarRegistro con respuesta vacia retorna Error`() {
        val resultado = ValidadorAutenticacion.validarRegistro(
            dni = "12345678",
            contrasena = "secreto123",
            nombre = "Juan",
            preguntaSeguridad = "¿Pregunta?",
            respuestaSeguridad = "",
        )
        assertIs<Resultado.Error>(resultado)
    }
}
