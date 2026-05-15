package com.vialgo.app.dominio.casosdeuso.autenticacion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import com.vialgo.app.dominio.validacion.ValidadorAutenticacion

data class ParamsRecuperacion(
    val dni: String,
    val respuestaSeguridad: String,
    val nuevaContrasena: String,
)

class RecuperarContrasenaUseCase(
    private val repositorio: RepositorioAutenticacion,
) : CasoDeUsoBase<ParamsRecuperacion, Unit> {

    override suspend fun ejecutar(parametros: ParamsRecuperacion): Resultado<Unit> {
        val validacionDni = ValidadorAutenticacion.validarDni(parametros.dni)
        if (validacionDni is Resultado.Error) return validacionDni

        val validacionContrasena = ValidadorAutenticacion.validarContrasena(
            parametros.nuevaContrasena,
            esRegistro = true,
        )
        if (validacionContrasena is Resultado.Error) return validacionContrasena

        if (parametros.respuestaSeguridad.isBlank()) {
            return Resultado.Error("La respuesta de seguridad no puede estar vacía")
        }

        return repositorio.recuperarContrasena(
            dni = parametros.dni,
            respuestaSeguridad = parametros.respuestaSeguridad,
            nuevaContrasena = parametros.nuevaContrasena,
        )
    }
}
