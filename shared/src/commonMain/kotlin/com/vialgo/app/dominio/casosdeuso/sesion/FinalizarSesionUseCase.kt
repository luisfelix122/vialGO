package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Sesion
import com.vialgo.app.dominio.repositorios.RepositorioSesion

data class ParamsFinalizarSesion(
    val sesionId: String,
    val xpGanado: Int,
)

class FinalizarSesionUseCase(
    private val repositorio: RepositorioSesion,
) : CasoDeUsoBase<ParamsFinalizarSesion, Sesion> {

    override suspend fun ejecutar(parametros: ParamsFinalizarSesion): Resultado<Sesion> =
        repositorio.finalizarSesion(
            sesionId = parametros.sesionId,
            xpGanado = parametros.xpGanado,
        )
}
