package com.vialgo.app.dominio.repositorios

import com.vialgo.app.dominio.comun.Resultado

interface RepositorioNotificaciones {
    suspend fun registrarTokenFcm(usuarioId: String, token: String): Resultado<Unit>
    suspend fun eliminarTokenFcm(usuarioId: String): Resultado<Unit>
}
