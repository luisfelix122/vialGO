package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import com.vialgo.app.dominio.repositorios.RepositorioSesion

data class ParamsResponderPregunta(
    val sesionId: String,
    val preguntaId: String,
    val opcionId: String,
    val fueCorrecta: Boolean,
    val tiempoRespuestaMs: Int,
    val xpObtenido: Int,
    val usuarioId: String,
)

class ResponderPreguntaUseCase(
    private val repoSesion: RepositorioSesion,
    private val repoGamificacion: RepositorioGamificacion,
) : CasoDeUsoBase<ParamsResponderPregunta, RespuestaUsuario> {

    override suspend fun ejecutar(parametros: ParamsResponderPregunta): Resultado<RespuestaUsuario> {
        val dto = RespuestaSesionDto(
            sesionId = parametros.sesionId,
            preguntaId = parametros.preguntaId,
            opcionId = parametros.opcionId,
            fueCorrecta = parametros.fueCorrecta,
            tiempoRespuestaMs = parametros.tiempoRespuestaMs,
            xpObtenido = parametros.xpObtenido,
        )
        val resultado = repoSesion.registrarRespuesta(dto)
        if (resultado is Resultado.Exito && !parametros.fueCorrecta) {
            repoGamificacion.consumirVida(parametros.usuarioId)
        }
        return resultado
    }
}
