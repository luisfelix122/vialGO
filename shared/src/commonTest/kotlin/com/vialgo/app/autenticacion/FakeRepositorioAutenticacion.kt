package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import kotlinx.datetime.Instant

class FakeRepositorioAutenticacion : RepositorioAutenticacion {

    var resultadoIniciarSesion: Resultado<Usuario> = Resultado.Exito(usuarioPrueba())
    var resultadoRegistrar: Resultado<Usuario> = Resultado.Exito(usuarioPrueba())
    var resultadoRecuperar: Resultado<Unit> = Resultado.Exito(Unit)
    var resultadoInvitado: Resultado<Usuario> = Resultado.Exito(usuarioInvitadoPrueba())
    var resultadoCerrarSesion: Resultado<Unit> = Resultado.Exito(Unit)
    var resultadoObtenerUsuario: Resultado<Usuario?> = Resultado.Exito(usuarioPrueba())

    var llamadasIniciarSesion = 0
    var llamadasRegistrar = 0
    var llamadasRecuperar = 0
    var llamadasInvitado = 0
    var llamadasCerrarSesion = 0
    var llamadasObtenerUsuario = 0

    override suspend fun iniciarSesion(dni: String, contrasena: String): Resultado<Usuario> {
        llamadasIniciarSesion++
        return resultadoIniciarSesion
    }

    override suspend fun registrar(
        dni: String,
        contrasena: String,
        nombre: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String,
        rolActivo: String,
        compromisoMinutos: Int,
    ): Resultado<Usuario> {
        llamadasRegistrar++
        return resultadoRegistrar
    }

    override suspend fun recuperarContrasena(
        dni: String,
        respuestaSeguridad: String,
        nuevaContrasena: String,
    ): Resultado<Unit> {
        llamadasRecuperar++
        return resultadoRecuperar
    }

    override suspend fun continuarComoInvitado(): Resultado<Usuario> {
        llamadasInvitado++
        return resultadoInvitado
    }

    override suspend fun cerrarSesion(): Resultado<Unit> {
        llamadasCerrarSesion++
        return resultadoCerrarSesion
    }

    override suspend fun obtenerUsuarioActual(): Resultado<Usuario?> {
        llamadasObtenerUsuario++
        return resultadoObtenerUsuario
    }

    companion object {
        fun usuarioPrueba(
            id: String = "usuario-id-123",
            dni: String = "12345678",
            nombre: String = "Juan Perez",
            rolActivo: String = "conductor",
        ) = Usuario(
            id = id,
            correo = "",
            nombre = nombre,
            rol = RolUsuario.CONDUCTOR,
            vidas = 5,
            rachaActual = 0,
            rachaMasLarga = 0,
            puntosExperiencia = 0,
            nivel = 1,
            creadoEn = Instant.fromEpochMilliseconds(0),
            actualizadoEn = Instant.fromEpochMilliseconds(0),
            dni = dni,
            rolActivo = rolActivo,
            compromisoMinutos = 30,
            tutorialCompletado = false,
            debeCambiarPregunta = false,
        )

        fun usuarioInvitadoPrueba() = Usuario(
            id = "invitado-id-456",
            correo = "",
            nombre = "Invitado",
            rol = RolUsuario.INVITADO,
            vidas = 0,
            rachaActual = 0,
            rachaMasLarga = 0,
            puntosExperiencia = 0,
            nivel = 0,
            creadoEn = Instant.fromEpochMilliseconds(0),
            actualizadoEn = Instant.fromEpochMilliseconds(0),
            dni = "",
            rolActivo = "invitado",
            compromisoMinutos = 0,
            tutorialCompletado = false,
            debeCambiarPregunta = false,
        )
    }
}
