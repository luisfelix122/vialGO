package com.vialgo.app.sesion

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.Beneficio
import com.vialgo.app.dominio.entidades.Clasificacion
import com.vialgo.app.dominio.entidades.Leccion
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.OpcionPregunta
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Sesion
import com.vialgo.app.dominio.entidades.Vida
import com.vialgo.app.dominio.repositorios.RepositorioContenido
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import com.vialgo.app.dominio.repositorios.RepositorioSesion
import kotlinx.datetime.Instant

// ---- Fake RepositorioSesion ----

class FakeRepositorioSesion : RepositorioSesion {

    var resultadoIniciarSesion: Resultado<Sesion> = Resultado.Exito(sesionPrueba())
    var resultadoRegistrarRespuesta: Resultado<RespuestaUsuario> = Resultado.Exito(respuestaPrueba())
    var resultadoFinalizarSesion: Resultado<Sesion> = Resultado.Exito(sesionPrueba())
    var resultadoObtenerSesion: Resultado<Sesion> = Resultado.Exito(sesionPrueba())
    var resultadoObtenerHistorial: Resultado<List<Sesion>> = Resultado.Exito(emptyList())

    var llamadasIniciarSesion = 0
    var llamadasRegistrarRespuesta = 0
    var llamadasFinalizarSesion = 0

    override suspend fun iniciarSesion(
        usuarioId: String,
        leccionId: String?,
        tipo: String,
        rolId: String,
    ): Resultado<Sesion> {
        llamadasIniciarSesion++
        return resultadoIniciarSesion
    }

    override suspend fun registrarRespuesta(respuesta: RespuestaSesionDto): Resultado<RespuestaUsuario> {
        llamadasRegistrarRespuesta++
        return resultadoRegistrarRespuesta
    }

    override suspend fun finalizarSesion(sesionId: String, xpGanado: Int): Resultado<Sesion> {
        llamadasFinalizarSesion++
        return resultadoFinalizarSesion
    }

    override suspend fun obtenerSesion(sesionId: String): Resultado<Sesion> =
        resultadoObtenerSesion

    override suspend fun obtenerHistorial(usuarioId: String): Resultado<List<Sesion>> =
        resultadoObtenerHistorial
}

// ---- Fake RepositorioContenido ----

class FakeRepositorioContenido : RepositorioContenido {

    var resultadoObtenerModulos: Resultado<List<Modulo>> = Resultado.Exito(emptyList())
    var resultadoObtenerPreguntas: Resultado<List<Pregunta>> = Resultado.Exito(preguntasPrueba())
    var resultadoObtenerProgreso: Resultado<List<ProgresoLeccion>> = Resultado.Exito(emptyList())

    var llamadasObtenerModulos = 0
    var llamadasObtenerPreguntas = 0
    var llamadasObtenerProgreso = 0

    override suspend fun obtenerModulos(rol: RolUsuario): Resultado<List<Modulo>> {
        llamadasObtenerModulos++
        return resultadoObtenerModulos
    }

    override suspend fun obtenerModulo(moduloId: String): Resultado<Modulo> =
        Resultado.Error("no implementado en fake")

    override suspend fun obtenerLeccion(leccionId: String): Resultado<Leccion> =
        Resultado.Error("no implementado en fake")

    override suspend fun obtenerPreguntas(leccionId: String): Resultado<List<Pregunta>> {
        llamadasObtenerPreguntas++
        return resultadoObtenerPreguntas
    }

    override suspend fun obtenerPreguntasPorCategoria(categoria: String): Resultado<List<Pregunta>> =
        resultadoObtenerPreguntas

    override suspend fun obtenerProgreso(usuarioId: String, rolId: String): Resultado<List<ProgresoLeccion>> {
        llamadasObtenerProgreso++
        return resultadoObtenerProgreso
    }
}

// ---- Fake RepositorioGamificacion ----

class FakeRepositorioGamificacion : RepositorioGamificacion {

    var resultadoObtenerVidas: Resultado<Vida> = Resultado.Exito(vidaPrueba())
    var resultadoConsumirVida: Resultado<Vida> = Resultado.Exito(vidaPrueba(vidasActuales = 4))

    var llamadasObtenerVidas = 0
    var llamadasConsumirVida = 0

    override suspend fun obtenerVidas(usuarioId: String): Resultado<Vida> {
        llamadasObtenerVidas++
        return resultadoObtenerVidas
    }

    override suspend fun consumirVida(usuarioId: String): Resultado<Vida> {
        llamadasConsumirVida++
        return resultadoConsumirVida
    }

    override suspend fun obtenerClasificacion(limite: Int): Resultado<List<Clasificacion>> =
        Resultado.Exito(emptyList())

    override suspend fun obtenerBeneficios(): Resultado<List<Beneficio>> =
        Resultado.Exito(emptyList())

    override suspend fun canjearBeneficio(usuarioId: String, beneficioId: String): Resultado<Unit> =
        Resultado.Exito(Unit)
}

// ---- Test data builders ----

fun sesionPrueba(
    id: String = "sesion-123",
    usuarioId: String = "usuario-123",
    xpGanado: Int = 0,
    preguntasTotales: Int = 5,
) = Sesion(
    id = id,
    usuarioId = usuarioId,
    leccionId = "leccion-1",
    rol = "conductor",
    tipo = "normal",
    estado = "en_progreso",
    fueMinimizada = false,
    xpGanado = xpGanado,
    preguntasTotales = preguntasTotales,
    iniciadaEn = Instant.fromEpochMilliseconds(0),
    completadaEn = null,
)

fun respuestaPrueba(
    fueCorrecta: Boolean = true,
    xpObtenido: Int = 50,
) = RespuestaUsuario(
    id = "respuesta-1",
    sesionId = "sesion-123",
    preguntaId = "pregunta-1",
    opcionId = "opcion-1",
    fueCorrecta = fueCorrecta,
    tiempoRespuestaMs = 1000,
    xpObtenido = xpObtenido,
)

fun vidaPrueba(vidasActuales: Int = 5) = Vida(
    id = "vida-1",
    usuarioId = "usuario-123",
    vidasActuales = vidasActuales,
    ultimaRecarga = Instant.fromEpochMilliseconds(0),
    actualizadoEn = Instant.fromEpochMilliseconds(0),
)

fun preguntasPrueba(cantidad: Int = 5): List<Pregunta> = (1..cantidad).map { i ->
    Pregunta(
        id = "pregunta-$i",
        categoriaId = "categoria-1",
        leccionId = "leccion-1",
        enunciado = "Pregunta $i",
        tipoMedio = "video",
        urlMedio = "https://example.com/video-$i.mp4",
        duracionMedioSeg = null,
        textoConsecuencia = "Consecuencia de pregunta $i",
        esClasificacion = false,
        estaActiva = true,
        opciones = listOf(
            OpcionPregunta(id = "opcion-correcta-$i", preguntaId = "pregunta-$i", texto = "Correcta", esCorrecta = true, orden = 1),
            OpcionPregunta(id = "opcion-incorrecta-$i", preguntaId = "pregunta-$i", texto = "Incorrecta", esCorrecta = false, orden = 2),
        ),
    )
}
