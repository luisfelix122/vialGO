package com.vialgo.app.datos.repositorios

import com.vialgo.app.datos.dtos.AuthResponseDto
import com.vialgo.app.datos.dtos.ErrorResponseDto
import com.vialgo.app.datos.dtos.LoginRequestDto
import com.vialgo.app.datos.dtos.RecuperacionRequestDto
import com.vialgo.app.datos.dtos.RegistroRequestDto
import com.vialgo.app.datos.mapeadores.ErrorMapeador
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.signInAnonymously
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class RepositorioAutenticacionImpl(
    private val cliente: SupabaseClient,
) : RepositorioAutenticacion {

    private val json = Json { ignoreUnknownKeys = true }

    private fun extraerError(cuerpo: String): String = try {
        json.decodeFromString<ErrorResponseDto>(cuerpo).error
    } catch (_: Exception) {
        cuerpo
    }

    override suspend fun iniciarSesion(dni: String, contrasena: String): Resultado<Usuario> =
        try {
            val respuesta = cliente.functions.invoke(
                function = "auth-login",
                body = LoginRequestDto(dni = dni, password = contrasena),
            )
            val cuerpo = respuesta.body<String>()
            if (cuerpo.contains("\"error\"")) {
                val errorMsg = extraerError(cuerpo)
                return Resultado.Error(ErrorMapeador.traducir(errorMsg))
            }
            val authResponse = json.decodeFromString<AuthResponseDto>(cuerpo)
            val usuario = authResponse.data?.user?.aEntidad()
                ?: return Resultado.Error("Respuesta de servidor inválida")
            Resultado.Exito(usuario)
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun registrar(
        dni: String,
        contrasena: String,
        nombre: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String,
        rolActivo: String,
        compromisoMinutos: Int,
    ): Resultado<Usuario> =
        try {
            val respuesta = cliente.functions.invoke(
                function = "auth-register",
                body = RegistroRequestDto(
                    dni = dni,
                    password = contrasena,
                    nombre = nombre,
                    preguntaSeguridad = preguntaSeguridad,
                    respuestaSeguridad = respuestaSeguridad,
                    rolActivo = rolActivo,
                    compromisoMinutos = compromisoMinutos,
                ),
            )
            val cuerpoReg = respuesta.body<String>()
            if (cuerpoReg.contains("\"error\"")) {
                val errorMsg = extraerError(cuerpoReg)
                return Resultado.Error(ErrorMapeador.traducir(errorMsg))
            }
            val authResponse = json.decodeFromString<AuthResponseDto>(cuerpoReg)
            val usuario = authResponse.data?.user?.aEntidad()
                ?: return Resultado.Error("Respuesta de servidor inválida")
            Resultado.Exito(usuario)
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun recuperarContrasena(
        dni: String,
        respuestaSeguridad: String,
        nuevaContrasena: String,
    ): Resultado<Unit> =
        try {
            val respuesta = cliente.functions.invoke(
                function = "auth-recover",
                body = RecuperacionRequestDto(
                    dni = dni,
                    respuestaSeguridad = respuestaSeguridad,
                    nuevaPassword = nuevaContrasena,
                ),
            )
            val cuerpo = respuesta.body<String>()
            if (cuerpo.contains("\"error\"")) {
                Resultado.Error(ErrorMapeador.traducir(extraerError(cuerpo)))
            } else {
                Resultado.Exito(Unit)
            }
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun continuarComoInvitado(): Resultado<Usuario> =
        try {
            cliente.auth.signInAnonymously()
            val sesion = cliente.auth.currentSessionOrNull()
            val usuarioInvitado = Usuario(
                id = sesion?.user?.id ?: "invitado",
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
                rolActivo = RolUsuario.INVITADO.name.lowercase(),
                compromisoMinutos = 0,
                tutorialCompletado = false,
                debeCambiarPregunta = false,
            )
            Resultado.Exito(usuarioInvitado)
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun cerrarSesion(): Resultado<Unit> =
        try {
            cliente.auth.signOut()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }

    override suspend fun obtenerUsuarioActual(): Resultado<Usuario?> =
        try {
            val sesion = cliente.auth.currentSessionOrNull()
            if (sesion == null) {
                Resultado.Exito(null)
            } else {
                val authUser = sesion.user
                if (authUser == null) {
                    Resultado.Exito(null)
                } else {
                    val usuario = Usuario(
                        id = authUser.id,
                        correo = authUser.email ?: "",
                        nombre = "",
                        rol = RolUsuario.CONDUCTOR,
                        vidas = 0,
                        rachaActual = 0,
                        rachaMasLarga = 0,
                        puntosExperiencia = 0,
                        nivel = 0,
                        creadoEn = Instant.fromEpochMilliseconds(0),
                        actualizadoEn = Instant.fromEpochMilliseconds(0),
                    )
                    Resultado.Exito(usuario)
                }
            }
        } catch (e: Exception) {
            Resultado.Error(ErrorMapeador.traducir(e.message), e)
        }
}
