package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Sesion
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import com.vialgo.app.dominio.repositorios.RepositorioSesion

data class ParamsIniciarSesionJuego(
    val usuarioId: String,
    val leccionId: String?,
    val tipo: String,
    val rol: String,
)

class IniciarSesionJuegoUseCase(
    private val repoSesion: RepositorioSesion,
    private val repoGamificacion: RepositorioGamificacion,
) : CasoDeUsoBase<ParamsIniciarSesionJuego, Sesion> {

    override suspend fun ejecutar(parametros: ParamsIniciarSesionJuego): Resultado<Sesion> {
        val vidasResult = repoGamificacion.obtenerVidas(parametros.usuarioId)
        if (vidasResult is Resultado.Exito && vidasResult.datos.cantidad <= 0) {
            return Resultado.Error("No tienes vidas disponibles")
        }
        return repoSesion.iniciarSesion(
            usuarioId = parametros.usuarioId,
            leccionId = parametros.leccionId,
            tipo = parametros.tipo,
            rolId = parametros.rol,
        )
    }
}
