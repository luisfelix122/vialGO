package com.vialgo.app.dominio.casosdeuso.autenticacion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import com.vialgo.app.dominio.validacion.ValidadorAutenticacion

data class ParamsRegistro(
    val dni: String,
    val contrasena: String,
    val nombre: String,
    val preguntaSeguridad: String,
    val respuestaSeguridad: String,
    val rolActivo: String,
    val compromisoMinutos: Int,
)

class RegistrarUsuarioUseCase(
    private val repositorio: RepositorioAutenticacion,
) : CasoDeUsoBase<ParamsRegistro, Usuario> {

    override suspend fun ejecutar(parametros: ParamsRegistro): Resultado<Usuario> {
        val validacion = ValidadorAutenticacion.validarRegistro(
            dni = parametros.dni,
            contrasena = parametros.contrasena,
            nombre = parametros.nombre,
            preguntaSeguridad = parametros.preguntaSeguridad,
            respuestaSeguridad = parametros.respuestaSeguridad,
        )
        if (validacion is Resultado.Error) return validacion

        return repositorio.registrar(
            dni = parametros.dni,
            contrasena = parametros.contrasena,
            nombre = parametros.nombre,
            preguntaSeguridad = parametros.preguntaSeguridad,
            respuestaSeguridad = parametros.respuestaSeguridad,
            rolActivo = parametros.rolActivo,
            compromisoMinutos = parametros.compromisoMinutos,
        )
    }
}
