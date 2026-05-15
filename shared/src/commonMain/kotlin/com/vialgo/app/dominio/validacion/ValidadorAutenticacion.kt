package com.vialgo.app.dominio.validacion

import com.vialgo.app.dominio.comun.Resultado

object ValidadorAutenticacion {

    fun validarDni(dni: String): Resultado<Unit> {
        if (dni.length != 8) {
            return Resultado.Error("El DNI debe tener exactamente 8 dígitos")
        }
        if (!dni.all { it.isDigit() }) {
            return Resultado.Error("El DNI solo puede contener dígitos numéricos")
        }
        return Resultado.Exito(Unit)
    }

    fun validarContrasena(contrasena: String, esRegistro: Boolean = false): Resultado<Unit> {
        if (contrasena.isEmpty()) {
            return Resultado.Error("La contraseña no puede estar vacía")
        }
        if (esRegistro && contrasena.length < 6) {
            return Resultado.Error("La contraseña debe tener al menos 6 caracteres")
        }
        return Resultado.Exito(Unit)
    }

    fun validarRegistro(
        dni: String,
        contrasena: String,
        nombre: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String,
    ): Resultado<Unit> {
        val resultadoDni = validarDni(dni)
        if (resultadoDni is Resultado.Error) return resultadoDni

        val resultadoContrasena = validarContrasena(contrasena, esRegistro = true)
        if (resultadoContrasena is Resultado.Error) return resultadoContrasena

        if (nombre.isBlank()) {
            return Resultado.Error("El nombre no puede estar vacío")
        }
        if (preguntaSeguridad.isBlank()) {
            return Resultado.Error("La pregunta de seguridad no puede estar vacía")
        }
        if (respuestaSeguridad.isBlank()) {
            return Resultado.Error("La respuesta de seguridad no puede estar vacía")
        }

        return Resultado.Exito(Unit)
    }
}
