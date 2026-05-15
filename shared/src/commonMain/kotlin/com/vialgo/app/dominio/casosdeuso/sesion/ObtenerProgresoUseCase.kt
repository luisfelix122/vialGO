package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.repositorios.RepositorioContenido

data class ParamsObtenerProgreso(val usuarioId: String, val rol: String)

class ObtenerProgresoUseCase(
    private val repositorio: RepositorioContenido,
) : CasoDeUsoBase<ParamsObtenerProgreso, List<ProgresoLeccion>> {

    override suspend fun ejecutar(parametros: ParamsObtenerProgreso): Resultado<List<ProgresoLeccion>> =
        repositorio.obtenerProgreso(parametros.usuarioId, parametros.rol)
}
