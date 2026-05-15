package com.vialgo.app.dominio.repositorios

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.entidades.Sesion

interface RepositorioSesion {
    suspend fun iniciarSesion(usuarioId: String, leccionId: String?, tipo: String, rolId: String): Resultado<Sesion>
    suspend fun registrarRespuesta(respuesta: RespuestaSesionDto): Resultado<RespuestaUsuario>
    suspend fun finalizarSesion(sesionId: String, xpGanado: Int): Resultado<Sesion>
    suspend fun obtenerSesion(sesionId: String): Resultado<Sesion>
    suspend fun obtenerHistorial(usuarioId: String): Resultado<List<Sesion>>
}
