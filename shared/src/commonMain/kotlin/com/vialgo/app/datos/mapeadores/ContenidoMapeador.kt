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
import kotlinx.datetime.Instant

fun ModuloDto.aEntidad(lecciones: List<Leccion> = emptyList()): Modulo = Modulo(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    orden = orden,
    rol = rol,
    estaActivo = estaActivo,
    lecciones = lecciones,
)

fun LeccionDto.aEntidad(): Leccion = Leccion(
    id = id,
    moduloId = moduloId,
    nombre = nombre,
    descripcion = descripcion,
    orden = orden,
    estaActiva = estaActiva,
)

fun PreguntaDto.aEntidad(opciones: List<OpcionPregunta> = emptyList()): Pregunta = Pregunta(
    id = id,
    categoriaId = categoriaId,
    leccionId = leccionId,
    enunciado = enunciado,
    tipoMedio = tipoMedio,
    urlMedio = urlMedio,
    duracionMedioSeg = duracionMedioSeg,
    textoConsecuencia = textoConsecuencia,
    esClasificacion = esClasificacion,
    estaActiva = estaActiva,
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
