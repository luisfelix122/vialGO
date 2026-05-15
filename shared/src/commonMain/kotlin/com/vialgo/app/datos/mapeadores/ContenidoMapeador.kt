package com.vialgo.app.datos.mapeadores

import com.vialgo.app.datos.dtos.LeccionDto
import com.vialgo.app.datos.dtos.ModuloDto
import com.vialgo.app.datos.dtos.OpcionPreguntaDto
import com.vialgo.app.datos.dtos.PreguntaDto
import com.vialgo.app.datos.dtos.ProgresoLeccionDto
import com.vialgo.app.dominio.entidades.Leccion
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.OpcionPregunta
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.TipoPregunta
import kotlinx.datetime.Instant

fun ModuloDto.aEntidad(lecciones: List<Leccion> = emptyList()): Modulo = Modulo(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    orden = orden,
    urlImagenPortada = urlImagenPortada,
    rolesDisponibles = rolesDisponibles.map { rolStringAEntidad(it) },
    lecciones = lecciones,
)

fun LeccionDto.aEntidad(): Leccion = Leccion(
    id = id,
    moduloId = moduloId,
    titulo = titulo,
    descripcion = descripcion,
    orden = orden,
    puntajeMaximo = puntajeMaximo,
    tiempoLimiteSegundos = tiempoLimiteSegundos,
    urlImagenPortada = urlImagenPortada,
)

fun PreguntaDto.aEntidad(opciones: List<OpcionPregunta> = emptyList()): Pregunta = Pregunta(
    id = id,
    leccionId = leccionId,
    enunciado = enunciado,
    tipo = TipoPregunta.valueOf(tipo.uppercase()),
    urlImagen = urlImagen,
    urlVideo = urlVideo,
    orden = orden,
    opciones = opciones,
)

fun OpcionPreguntaDto.aEntidad(): OpcionPregunta = OpcionPregunta(
    id = id,
    preguntaId = preguntaId,
    texto = texto,
    esCorrecta = esCorrecta,
    orden = orden,
)

fun ProgresoLeccionDto.aEntidad(): ProgresoLeccion = ProgresoLeccion(
    id = id,
    usuarioId = usuarioId,
    leccionId = leccionId,
    rol = rol,
    completada = completada,
    estrellas = estrellas,
    mejorXp = mejorXp,
    completadaEn = completadaEn?.let { Instant.parse(it) },
    actualizadoEn = Instant.parse(actualizadoEn),
)
