package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.BeneficioDto
import com.vialgo.app.datos.dtos.ClasificacionDto
import com.vialgo.app.datos.dtos.VidaDto
import com.vialgo.app.datos.mapeadores.aEntidad
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GamificacionMapeadorTest {

    // ---- VidaDto ----

    private val vidaDtoBase = VidaDto(
        id = "vida-001",
        usuarioId = "usuario-123",
        vidasActuales = 5,
        ultimaRecarga = "2024-06-01T08:00:00Z",
        actualizadoEn = "2024-06-01T08:00:00Z",
    )

    @Test
    fun `VidaDto aEntidad mapea todos los campos correctamente`() {
        val entidad = vidaDtoBase.aEntidad()

        assertEquals("vida-001", entidad.id)
        assertEquals("usuario-123", entidad.usuarioId)
        assertEquals(5, entidad.vidasActuales)
        assertEquals(Instant.parse("2024-06-01T08:00:00Z"), entidad.ultimaRecarga)
        assertEquals(Instant.parse("2024-06-01T08:00:00Z"), entidad.actualizadoEn)
    }

    @Test
    fun `VidaDto aEntidad con timestamps vacios usa epoch`() {
        val dto = vidaDtoBase.copy(ultimaRecarga = "", actualizadoEn = "")
        val entidad = dto.aEntidad()

        assertEquals(Instant.fromEpochMilliseconds(0), entidad.ultimaRecarga)
        assertEquals(Instant.fromEpochMilliseconds(0), entidad.actualizadoEn)
    }

    @Test
    fun `VidaDto aEntidad con cero vidas mapea vidasActuales correctamente`() {
        val dto = vidaDtoBase.copy(vidasActuales = 0)
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.vidasActuales)
    }

    // ---- BeneficioDto ----

    private val beneficioDtoBase = BeneficioDto(
        id = "beneficio-001",
        titulo = "Descuento en peaje",
        descripcion = "Obtene un descuento del 10% en peajes de autopista",
        imagenUrl = "https://example.com/imagen.png",
        rol = "conductor",
        reputacionMinima = 75.0,
        estaActivo = true,
        disponible = true,
        orden = 1,
    )

    @Test
    fun `BeneficioDto aEntidad mapea todos los campos correctamente`() {
        val entidad = beneficioDtoBase.aEntidad()

        assertEquals("beneficio-001", entidad.id)
        assertEquals("Descuento en peaje", entidad.titulo)
        assertEquals("Obtene un descuento del 10% en peajes de autopista", entidad.descripcion)
        assertEquals("https://example.com/imagen.png", entidad.imagenUrl)
        assertEquals("conductor", entidad.rol)
        assertEquals(75.0, entidad.reputacionMinima)
        assertEquals(true, entidad.estaActivo)
        assertEquals(true, entidad.disponible)
        assertEquals(1, entidad.orden)
    }

    @Test
    fun `BeneficioDto aEntidad con imagenUrl null mapea a null`() {
        val dto = beneficioDtoBase.copy(imagenUrl = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.imagenUrl)
    }

    @Test
    fun `BeneficioDto aEntidad no disponible mapea correctamente`() {
        val dto = beneficioDtoBase.copy(disponible = false)
        val entidad = dto.aEntidad()

        assertEquals(false, entidad.disponible)
    }

    @Test
    fun `BeneficioDto aEntidad con reputacion minima cero mapea correctamente`() {
        val dto = beneficioDtoBase.copy(reputacionMinima = 0.0)
        val entidad = dto.aEntidad()

        assertEquals(0.0, entidad.reputacionMinima)
    }

    // ---- ClasificacionDto ----

    private val clasificacionDtoBase = ClasificacionDto(
        id = "clas-001",
        usuarioId = "usuario-456",
        rol = "conductor",
        sesionId = "sesion-789",
        reputacionInicial = 85.5,
        completadaEn = "2024-06-01T08:00:00Z",
    )

    @Test
    fun `ClasificacionDto aEntidad mapea todos los campos correctamente`() {
        val entidad = clasificacionDtoBase.aEntidad()

        assertEquals("clas-001", entidad.id)
        assertEquals("usuario-456", entidad.usuarioId)
        assertEquals("conductor", entidad.rol)
        assertEquals("sesion-789", entidad.sesionId)
        assertEquals(85.5, entidad.reputacionInicial)
        assertEquals(Instant.parse("2024-06-01T08:00:00Z"), entidad.completadaEn)
    }

    @Test
    fun `ClasificacionDto aEntidad con completadaEn vacio mapea a null`() {
        val dto = clasificacionDtoBase.copy(completadaEn = "")
        val entidad = dto.aEntidad()

        assertNull(entidad.completadaEn)
    }

    @Test
    fun `ClasificacionDto aEntidad mapea rol peaton correctamente`() {
        val dto = clasificacionDtoBase.copy(rol = "peaton")
        val entidad = dto.aEntidad()

        assertEquals("peaton", entidad.rol)
    }
}
