package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.BeneficioDto
import com.vialgo.app.datos.dtos.ClasificacionDto
import com.vialgo.app.datos.dtos.VidaDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.entidades.RolUsuario
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GamificacionMapeadorTest {

    // ---- VidaDto ----

    private val vidaDtoBase = VidaDto(
        id = "vida-001",
        usuarioId = "usuario-123",
        cantidad = 5,
        proximaRecargaEn = "2024-06-01T08:00:00Z",
    )

    @Test
    fun `VidaDto aEntidad mapea todos los campos correctamente`() {
        val entidad = vidaDtoBase.aEntidad()

        assertEquals("vida-001", entidad.id)
        assertEquals("usuario-123", entidad.usuarioId)
        assertEquals(5, entidad.cantidad)
        assertEquals(Instant.parse("2024-06-01T08:00:00Z"), entidad.proximaRecargaEn)
    }

    @Test
    fun `VidaDto aEntidad con proximaRecargaEn null mapea a null`() {
        val dto = vidaDtoBase.copy(proximaRecargaEn = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.proximaRecargaEn)
    }

    @Test
    fun `VidaDto aEntidad con cero vidas mapea cantidad correctamente`() {
        val dto = vidaDtoBase.copy(cantidad = 0)
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.cantidad)
    }

    // ---- BeneficioDto ----

    private val beneficioDtoBase = BeneficioDto(
        id = "beneficio-001",
        titulo = "Descuento en peaje",
        descripcion = "Obtene un descuento del 10% en peajes de autopista",
        urlImagen = "https://example.com/imagen.png",
        puntosRequeridos = 500,
        categoria = "transporte",
        disponible = true,
    )

    @Test
    fun `BeneficioDto aEntidad mapea todos los campos correctamente`() {
        val entidad = beneficioDtoBase.aEntidad()

        assertEquals("beneficio-001", entidad.id)
        assertEquals("Descuento en peaje", entidad.titulo)
        assertEquals("Obtene un descuento del 10% en peajes de autopista", entidad.descripcion)
        assertEquals("https://example.com/imagen.png", entidad.urlImagen)
        assertEquals(500, entidad.puntosRequeridos)
        assertEquals("transporte", entidad.categoria)
        assertEquals(true, entidad.disponible)
    }

    @Test
    fun `BeneficioDto aEntidad con urlImagen null mapea a null`() {
        val dto = beneficioDtoBase.copy(urlImagen = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.urlImagen)
    }

    @Test
    fun `BeneficioDto aEntidad no disponible mapea correctamente`() {
        val dto = beneficioDtoBase.copy(disponible = false)
        val entidad = dto.aEntidad()

        assertEquals(false, entidad.disponible)
    }

    @Test
    fun `BeneficioDto aEntidad con cero puntos requeridos mapea correctamente`() {
        val dto = beneficioDtoBase.copy(puntosRequeridos = 0)
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.puntosRequeridos)
    }

    // ---- ClasificacionDto ----

    private val clasificacionDtoBase = ClasificacionDto(
        posicion = 1,
        usuarioId = "usuario-456",
        nombreUsuario = "Maria Lopez",
        puntaje = 1200,
        nivel = 5,
        rolUsuario = "conductor",
    )

    @Test
    fun `ClasificacionDto aEntidad mapea todos los campos correctamente`() {
        val entidad = clasificacionDtoBase.aEntidad()

        assertEquals(1, entidad.posicion)
        assertEquals("usuario-456", entidad.usuarioId)
        assertEquals("Maria Lopez", entidad.nombreUsuario)
        assertEquals(1200, entidad.puntaje)
        assertEquals(5, entidad.nivel)
        assertEquals(RolUsuario.CONDUCTOR, entidad.rolUsuario)
    }

    @Test
    fun `ClasificacionDto aEntidad mapea rol peaton correctamente`() {
        val dto = clasificacionDtoBase.copy(rolUsuario = "peaton")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.PEATONAL, entidad.rolUsuario)
    }

    @Test
    fun `ClasificacionDto aEntidad mapea rol ciclista correctamente`() {
        val dto = clasificacionDtoBase.copy(rolUsuario = "ciclista")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.CICLISTA, entidad.rolUsuario)
    }

    @Test
    fun `ClasificacionDto aEntidad mapea posicion correctamente`() {
        val dto = clasificacionDtoBase.copy(posicion = 10)
        val entidad = dto.aEntidad()

        assertEquals(10, entidad.posicion)
    }
}
