package com.vialgo.app.dominio.repositorios

import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Leccion
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario

interface RepositorioContenido {
    suspend fun obtenerModulos(rol: RolUsuario): Resultado<List<Modulo>>
    suspend fun obtenerModulo(moduloId: String): Resultado<Modulo>
    suspend fun obtenerLeccion(leccionId: String): Resultado<Leccion>
    suspend fun obtenerPreguntas(leccionId: String): Resultado<List<Pregunta>>
    suspend fun obtenerPreguntasPorCategoria(categoria: String): Resultado<List<Pregunta>>
    suspend fun obtenerProgreso(usuarioId: String, rolId: String): Resultado<List<ProgresoLeccion>>
}
