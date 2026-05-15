package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.repositorios.RepositorioContenido

data class ParamsObtenerPreguntas(val leccionId: String)

class ObtenerPreguntasUseCase(
    private val repositorio: RepositorioContenido,
) : CasoDeUsoBase<ParamsObtenerPreguntas, List<Pregunta>> {

    override suspend fun ejecutar(parametros: ParamsObtenerPreguntas): Resultado<List<Pregunta>> =
        repositorio.obtenerPreguntas(parametros.leccionId)
}
