package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.RespuestaSesionDto
import com.vialgo.app.datos.dtos.SesionDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.datos.mapeadores.aDto
import com.vialgo.app.dominio.entidades.RespuestaUsuario
import com.vialgo.app.dominio.entidades.Sesion
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SesionMapeadorTest {

    private val sesionDtoBase = SesionDto(
        id = "sesion-123",
        usuarioId = "usuario-456",
        leccionId = "leccion-789",
        rol = "conductor",
        tipo = "normal",
        estado = "en_progreso",
        fueMinimizada = false,
        iniciadaEn = "2024-01-01T10:00:00Z",
        completadaEn = null,
        xpGanado = 0,
        preguntasTotales = 5,
    )

    @Test
    fun `SesionDto aEntidad mapea todos los campos correctamente`() {
        val entidad = sesionDtoBase.aEntidad()

        assertEquals("sesion-123", entidad.id)
        assertEquals("usuario-456", entidad.usuarioId)
        assertEquals("leccion-789", entidad.leccionId)
        assertEquals("conductor", entidad.rol)
        assertEquals("normal", entidad.tipo)
        assertEquals("en_progreso", entidad.estado)
        assertEquals(false, entidad.fueMinimizada)
        assertEquals(0, entidad.xpGanado)
        assertEquals(5, entidad.preguntasTotales)
        assertEquals(Instant.parse("2024-01-01T10:00:00Z"), entidad.iniciadaEn)
        assertNull(entidad.completadaEn)
    }

    @Test
    fun `SesionDto con leccionId null mapea correctamente`() {
        val dto = sesionDtoBase.copy(leccionId = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.leccionId)
    }

    @Test
    fun `SesionDto con completadaEn mapea fecha correctamente`() {
        val dto = sesionDtoBase.copy(completadaEn = "2024-01-01T11:00:00Z")
        val entidad = dto.aEntidad()

        assertEquals(Instant.parse("2024-01-01T11:00:00Z"), entidad.completadaEn)
    }

    @Test
    fun `SesionDto con estado completada mapea correctamente`() {
        val dto = sesionDtoBase.copy(estado = "completada", xpGanado = 150)
        val entidad = dto.aEntidad()

        assertEquals("completada", entidad.estado)
        assertEquals(150, entidad.xpGanado)
    }

    @Test
    fun `Sesion aDto round-trip mantiene todos los campos`() {
        val entidad = sesionDtoBase.aEntidad()
        val dto = entidad.aDto()

        assertEquals(entidad.id, dto.id)
        assertEquals(entidad.usuarioId, dto.usuarioId)
        assertEquals(entidad.leccionId, dto.leccionId)
        assertEquals(entidad.rol, dto.rol)
        assertEquals(entidad.tipo, dto.tipo)
        assertEquals(entidad.estado, dto.estado)
        assertEquals(entidad.fueMinimizada, dto.fueMinimizada)
        assertEquals(entidad.xpGanado, dto.xpGanado)
        assertEquals(entidad.preguntasTotales, dto.preguntasTotales)
        assertNull(dto.completadaEn)
    }

    @Test
    fun `RespuestaSesionDto aEntidad mapea xpObtenido correctamente`() {
        val dto = RespuestaSesionDto(
            id = "resp-001",
            sesionId = "sesion-123",
            preguntaId = "preg-456",
            opcionId = "opc-789",
            fueCorrecta = true,
            tiempoRespuestaMs = 1500,
            xpObtenido = 50,
            esReintento = false,
        )
        val entidad = dto.aEntidad()

        assertEquals("resp-001", entidad.id)
        assertEquals("sesion-123", entidad.sesionId)
        assertEquals("preg-456", entidad.preguntaId)
        assertEquals("opc-789", entidad.opcionId)
        assertEquals(true, entidad.fueCorrecta)
        assertEquals(1500, entidad.tiempoRespuestaMs)
        assertEquals(50, entidad.xpObtenido)
        assertEquals(false, entidad.esReintento)
    }

    @Test
    fun `RespuestaSesionDto con xpObtenido null mapea a 0`() {
        val dto = RespuestaSesionDto(
            id = "",
            sesionId = "sesion-123",
            preguntaId = "preg-456",
            opcionId = "opc-789",
            fueCorrecta = false,
            tiempoRespuestaMs = 5000,
            xpObtenido = null,
        )
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.xpObtenido)
    }
}
