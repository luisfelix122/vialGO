package com.vialgo.app.dominio.casosdeuso.sesion

import com.vialgo.app.dominio.casosdeuso.CasoDeUsoBase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.repositorios.RepositorioContenido

data class ParamsObtenerModulos(val rol: RolUsuario)

class ObtenerModulosUseCase(
    private val repositorio: RepositorioContenido,
) : CasoDeUsoBase<ParamsObtenerModulos, List<Modulo>> {

    override suspend fun ejecutar(parametros: ParamsObtenerModulos): Resultado<List<Modulo>> =
        repositorio.obtenerModulos(parametros.rol)
}
