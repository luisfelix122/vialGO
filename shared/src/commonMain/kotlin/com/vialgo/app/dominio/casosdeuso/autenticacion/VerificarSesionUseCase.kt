package com.vialgo.app.dominio.casosdeuso.autenticacion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoSinParametros
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Usuario
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion

class VerificarSesionUseCase(
    private val repositorio: RepositorioAutenticacion,
) : CasoDeUsoSinParametros<Usuario?> {

    override suspend fun ejecutar(): Resultado<Usuario?> =
        repositorio.obtenerUsuarioActual()
}
