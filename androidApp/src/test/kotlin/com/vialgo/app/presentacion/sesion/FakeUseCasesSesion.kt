package com.vialgo.app.presentacion.sesion

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.dominio.casosdeuso.sesion.FinalizarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.IniciarSesionJuegoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerModulosUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerPreguntasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerProgresoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerVidasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsFinalizarSesion
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsIniciarSesionJuego
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerModulos
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerPreguntas
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerProgreso
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsObtenerVidas
import com.vialgo.app.dominio.casosdeuso.sesion.ParamsResponderPregunta
import com.vialgo.app.dominio.casosdeuso.sesion.ResponderPreguntaUseCase
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
import com.vialgo.app.dominio.entidades.TipoPregunta
import com.vialgo.app.dominio.entidades.Vida
import com.vialgo.app.dominio.repositorios.RepositorioContenido
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import com.vialgo.app.dominio.repositorios.RepositorioSesion
import kotlinx.datetime.Instant

// ---- Fake repos ----

class FakeRepoSesionAndroid(
    var resultadoIniciarSesion: Resultado<Sesion> = Resultado.Exito(sesionTestAndroid()),
    var resultadoRegistrarRespuesta: Resultado<RespuestaUsuario> = Resultado.Exito(respuestaTestAndroid()),
    var resultadoFinalizarSesion: Resultado<Sesion> = Resultado.Exito(sesionTestAndroid()),
    var resultadoObtenerSesion: Resultado<Sesion> = Resultado.Exito(sesionTestAndroid(xpGanado = 150)),
) : RepositorioSesion {
    var llamadasFinalizarSesion = 0

    override suspend fun iniciarSesion(usuarioId: String, leccionId: String?, tipo: String, rolId: String) = resultadoIniciarSesion
    override suspend fun registrarRespuesta(respuesta: RespuestaSesionDto) = resultadoRegistrarRespuesta
    override suspend fun finalizarSesion(sesionId: String, xpGanado: Int): Resultado<Sesion> {
        llamadasFinalizarSesion++
        return resultadoFinalizarSesion
    }
    override suspend fun obtenerSesion(sesionId: String) = resultadoObtenerSesion
    override suspend fun obtenerHistorial(usuarioId: String): Resultado<List<Sesion>> = Resultado.Exito(emptyList())
}

class FakeRepoContenidoAndroid(
    var resultadoObtenerModulos: Resultado<List<Modulo>> = Resultado.Exito(emptyList()),
    var resultadoObtenerPreguntas: Resultado<List<Pregunta>> = Resultado.Exito(preguntasTestAndroid()),
    var resultadoObtenerProgreso: Resultado<List<ProgresoLeccion>> = Resultado.Exito(emptyList()),
) : RepositorioContenido {
    override suspend fun obtenerModulos(rol: RolUsuario) = resultadoObtenerModulos
    override suspend fun obtenerModulo(moduloId: String): Resultado<Modulo> = Resultado.Error("no impl")
    override suspend fun obtenerLeccion(leccionId: String): Resultado<Leccion> = Resultado.Error("no impl")
    override suspend fun obtenerPreguntas(leccionId: String) = resultadoObtenerPreguntas
    override suspend fun obtenerPreguntasPorCategoria(categoria: String) = resultadoObtenerPreguntas
    override suspend fun obtenerProgreso(usuarioId: String, rolId: String) = resultadoObtenerProgreso
}

class FakeRepoGamificacionAndroid(
    var resultadoObtenerVidas: Resultado<Vida> = Resultado.Exito(vidaTestAndroid()),
    var resultadoConsumirVida: Resultado<Vida> = Resultado.Exito(vidaTestAndroid(cantidad = 4)),
) : RepositorioGamificacion {
    override suspend fun obtenerVidas(usuarioId: String) = resultadoObtenerVidas
    override suspend fun consumirVida(usuarioId: String) = resultadoConsumirVida
    override suspend fun obtenerClasificacion(limite: Int): Resultado<List<Clasificacion>> = Resultado.Exito(emptyList())
    override suspend fun obtenerBeneficios(): Resultado<List<Beneficio>> = Resultado.Exito(emptyList())
    override suspend fun canjearBeneficio(usuarioId: String, beneficioId: String): Resultado<Unit> = Resultado.Exito(Unit)
}

// ---- Test data builders ----

fun sesionTestAndroid(id: String = "sesion-123", xpGanado: Int = 0) = Sesion(
    id = id,
    usuarioId = "usuario-123",
    leccionId = "leccion-1",
    rol = "conductor",
    tipo = "normal",
    estado = "en_progreso",
    fueMinimizada = false,
    xpGanado = xpGanado,
    preguntasTotales = 5,
    iniciadaEn = Instant.fromEpochMilliseconds(0),
    completadaEn = null,
)

fun respuestaTestAndroid(fueCorrecta: Boolean = true, xpObtenido: Int = 50) = RespuestaUsuario(
    id = "respuesta-1",
    sesionId = "sesion-123",
    preguntaId = "pregunta-1",
    opcionId = "opcion-correcta-1",
    fueCorrecta = fueCorrecta,
    tiempoRespuestaMs = 1000,
    xpObtenido = xpObtenido,
)

fun vidaTestAndroid(cantidad: Int = 5) = Vida(
    id = "vida-1",
    usuarioId = "usuario-123",
    cantidad = cantidad,
    proximaRecargaEn = null,
)

fun preguntasTestAndroid(cantidad: Int = 5): List<Pregunta> = (1..cantidad).map { i ->
    Pregunta(
        id = "pregunta-$i",
        leccionId = "leccion-1",
        enunciado = "Pregunta $i",
        tipo = TipoPregunta.OPCION_MULTIPLE,
        urlImagen = null,
        urlVideo = null,
        orden = i,
        opciones = listOf(
            OpcionPregunta(id = "opcion-correcta-$i", preguntaId = "pregunta-$i", texto = "Correcta", esCorrecta = true, orden = 1),
            OpcionPregunta(id = "opcion-incorrecta-$i", preguntaId = "pregunta-$i", texto = "Incorrecta", esCorrecta = false, orden = 2),
        ),
    )
}

// ---- Use case factories ----

fun fakeIniciarSesionJuegoUseCase(
    repoSesion: RepositorioSesion = FakeRepoSesionAndroid(),
    repoGami: RepositorioGamificacion = FakeRepoGamificacionAndroid(),
) = IniciarSesionJuegoUseCase(repoSesion, repoGami)

fun fakeObtenerPreguntasUseCase(
    repo: RepositorioContenido = FakeRepoContenidoAndroid(),
) = ObtenerPreguntasUseCase(repo)

fun fakeResponderPreguntaUseCase(
    repoSesion: RepositorioSesion = FakeRepoSesionAndroid(),
    repoGami: RepositorioGamificacion = FakeRepoGamificacionAndroid(),
) = ResponderPreguntaUseCase(repoSesion, repoGami)

fun fakeFinalizarSesionUseCase(
    repo: RepositorioSesion = FakeRepoSesionAndroid(),
) = FinalizarSesionUseCase(repo)

fun fakeObtenerModulosUseCase(
    repo: RepositorioContenido = FakeRepoContenidoAndroid(),
) = ObtenerModulosUseCase(repo)

fun fakeObtenerProgresoUseCase(
    repo: RepositorioContenido = FakeRepoContenidoAndroid(),
) = ObtenerProgresoUseCase(repo)

fun fakeObtenerVidasUseCase(
    repo: RepositorioGamificacion = FakeRepoGamificacionAndroid(),
) = ObtenerVidasUseCase(repo)
