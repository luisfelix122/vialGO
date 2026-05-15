package com.vialgo.app.dominio.casosdeuso.autenticacion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import com.vialgo.app.dominio.validacion.ValidadorAutenticacion

data class ParamsLogin(val dni: String, val contrasena: String)

class IniciarSesionUseCase(
    private val repositorio: RepositorioAutenticacion,
) : CasoDeUsoBase<ParamsLogin, Usuario> {

    override suspend fun ejecutar(parametros: ParamsLogin): Resultado<Usuario> {
        val validacionDni = ValidadorAutenticacion.validarDni(parametros.dni)
        if (validacionDni is Resultado.Error) return validacionDni

        val validacionContrasena = ValidadorAutenticacion.validarContrasena(parametros.contrasena)
        if (validacionContrasena is Resultado.Error) return validacionContrasena

        return repositorio.iniciarSesion(parametros.dni, parametros.contrasena)
    }
}
