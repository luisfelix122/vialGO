package com.vialgo.app.dominio.casosdeuso.autenticacion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoSinParametros
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion

class CerrarSesionUseCase(
    private val repositorio: RepositorioAutenticacion,
) : CasoDeUsoSinParametros<Unit> {

    override suspend fun ejecutar(): Resultado<Unit> =
        repositorio.cerrarSesion()
}
