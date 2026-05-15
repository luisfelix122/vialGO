package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Vida
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion

data class ParamsObtenerVidas(val usuarioId: String)

class ObtenerVidasUseCase(
    private val repositorio: RepositorioGamificacion,
) : CasoDeUsoBase<ParamsObtenerVidas, Vida> {

    override suspend fun ejecutar(parametros: ParamsObtenerVidas): Resultado<Vida> =
        repositorio.obtenerVidas(parametros.usuarioId)
}
