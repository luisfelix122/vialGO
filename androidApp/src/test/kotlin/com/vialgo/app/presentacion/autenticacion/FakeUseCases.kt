package com.vialgo.app.presentacion.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionInvitadoUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsLogin
import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRecuperacion
import com.vialgo.app.dominio.casosdeuso.autenticacion.ParamsRegistro
import com.vialgo.app.dominio.casosdeuso.autenticacion.RecuperarContrasenaUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.RegistrarUsuarioUseCase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import kotlinx.datetime.Instant

// Repositorio de autenticacion minimo para instanciar los use cases reales
class FakeRepositorioAutenticacionAndroid(
    var resultadoIniciarSesion: Resultado<Usuario> = Resultado.Exito(usuarioPrueba()),
    var resultadoRegistrar: Resultado<Usuario> = Resultado.Exito(usuarioPrueba()),
    var resultadoRecuperar: Resultado<Unit> = Resultado.Exito(Unit),
    var resultadoInvitado: Resultado<Usuario> = Resultado.Exito(usuarioInvitadoPrueba()),
) : RepositorioAutenticacion {

    override suspend fun iniciarSesion(dni: String, contrasena: String): Resultado<Usuario> =
        resultadoIniciarSesion

    override suspend fun registrar(
        dni: String,
        contrasena: String,
        nombre: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String,
        rolActivo: String,
        compromisoMinutos: Int,
    ): Resultado<Usuario> = resultadoRegistrar

    override suspend fun recuperarContrasena(
        dni: String,
        respuestaSeguridad: String,
        nuevaContrasena: String,
    ): Resultado<Unit> = resultadoRecuperar

    override suspend fun continuarComoInvitado(): Resultado<Usuario> = resultadoInvitado

    override suspend fun cerrarSesion(): Resultado<Unit> = Resultado.Exito(Unit)

    override suspend fun obtenerUsuarioActual(): Resultado<Usuario?> = Resultado.Exito(null)
}

fun usuarioPrueba(
    tutorialCompletado: Boolean = false,
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
    tutorialCompletado = tutorialCompletado,
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

fun fakeIniciarSesionUseCase(resultado: Resultado<Usuario> = Resultado.Exito(usuarioPrueba())): IniciarSesionUseCase =
    IniciarSesionUseCase(FakeRepositorioAutenticacionAndroid(resultadoIniciarSesion = resultado))

fun fakeRegistrarUsuarioUseCase(resultado: Resultado<Usuario> = Resultado.Exito(usuarioPrueba())): RegistrarUsuarioUseCase =
    RegistrarUsuarioUseCase(FakeRepositorioAutenticacionAndroid(resultadoRegistrar = resultado))

fun fakeRecuperarContrasenaUseCase(resultado: Resultado<Unit> = Resultado.Exito(Unit)): RecuperarContrasenaUseCase =
    RecuperarContrasenaUseCase(FakeRepositorioAutenticacionAndroid(resultadoRecuperar = resultado))

fun fakeIniciarSesionInvitadoUseCase(resultado: Resultado<Usuario> = Resultado.Exito(usuarioInvitadoPrueba())): IniciarSesionInvitadoUseCase =
    IniciarSesionInvitadoUseCase(FakeRepositorioAutenticacionAndroid(resultadoInvitado = resultado))
