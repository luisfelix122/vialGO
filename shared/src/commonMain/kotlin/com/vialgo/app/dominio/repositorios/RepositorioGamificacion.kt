package com.vialgo.app.dominio.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Beneficio
import com.vialgo.app.dominio.entidades.Clasificacion
import com.vialgo.app.dominio.entidades.Vida

interface RepositorioGamificacion {
    suspend fun obtenerVidas(usuarioId: String): Resultado<Vida>
    suspend fun consumirVida(usuarioId: String): Resultado<Vida>
    suspend fun obtenerClasificacion(limite: Int): Resultado<List<Clasificacion>>
    suspend fun obtenerBeneficios(): Resultado<List<Beneficio>>
    suspend fun canjearBeneficio(usuarioId: String, beneficioId: String): Resultado<Unit>
}
