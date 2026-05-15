package com.vialgo.app.dominio.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Usuario

interface RepositorioAutenticacion {
    suspend fun iniciarSesion(dni: String, contrasena: String): Resultado<Usuario>
    suspend fun registrar(
        dni: String,
        contrasena: String,
        nombre: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String,
        rolActivo: String,
        compromisoMinutos: Int,
    ): Resultado<Usuario>
    suspend fun recuperarContrasena(
        dni: String,
        respuestaSeguridad: String,
        nuevaContrasena: String,
    ): Resultado<Unit>
    suspend fun continuarComoInvitado(): Resultado<Usuario>
    suspend fun cerrarSesion(): Resultado<Unit>
    suspend fun obtenerUsuarioActual(): Resultado<Usuario?>
}
