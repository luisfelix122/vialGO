package com.vialgo.app.dominio.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.EstadisticasUsuario
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario

interface RepositorioUsuario {
    suspend fun obtenerUsuario(usuarioId: String): Resultado<Usuario>
    suspend fun actualizarRol(usuarioId: String, rol: RolUsuario): Resultado<Usuario>
    suspend fun obtenerProgreso(usuarioId: String): Resultado<List<ProgresoLeccion>>
    suspend fun obtenerEstadisticas(usuarioId: String): Resultado<EstadisticasUsuario>
}
