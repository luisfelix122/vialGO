package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.ProgresoLeccionDto
import com.vialgo.app.datos.mapeadores.aEntidad
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProgresoLeccionMapeadorTest {

    private val progresoDtoBase = ProgresoLeccionDto(
        id = "prog-001",
        usuarioId = "usuario-123",
        leccionId = "leccion-456",
        rol = "conductor",
        completada = true,
        estrellas = 3,
        mejorXp = 250,
        completadaEn = "2024-01-01T10:00:00Z",
        actualizadoEn = "2024-01-01T10:00:00Z",
    )

    @Test
    fun `ProgresoLeccionDto aEntidad mapea todos los campos correctamente`() {
        val entidad = progresoDtoBase.aEntidad()

        assertEquals("prog-001", entidad.id)
        assertEquals("usuario-123", entidad.usuarioId)
        assertEquals("leccion-456", entidad.leccionId)
        assertEquals("conductor", entidad.rol)
        assertEquals(true, entidad.completada)
        assertEquals(3, entidad.estrellas)
        assertEquals(250, entidad.mejorXp)
        assertEquals(Instant.parse("2024-01-01T10:00:00Z"), entidad.completadaEn)
        assertEquals(Instant.parse("2024-01-01T10:00:00Z"), entidad.actualizadoEn)
    }

    @Test
    fun `ProgresoLeccionDto con cero estrellas mapea correctamente`() {
        val dto = progresoDtoBase.copy(estrellas = 0, completada = false)
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.estrellas)
        assertEquals(false, entidad.completada)
    }

    @Test
    fun `ProgresoLeccionDto con una estrella mapea correctamente`() {
        val dto = progresoDtoBase.copy(estrellas = 1)
        val entidad = dto.aEntidad()

        assertEquals(1, entidad.estrellas)
    }

    @Test
    fun `ProgresoLeccionDto con dos estrellas mapea correctamente`() {
        val dto = progresoDtoBase.copy(estrellas = 2)
        val entidad = dto.aEntidad()

        assertEquals(2, entidad.estrellas)
    }

    @Test
    fun `ProgresoLeccionDto con completadaEn null mapea a null`() {
        val dto = progresoDtoBase.copy(completadaEn = null, completada = false)
        val entidad = dto.aEntidad()

        assertNull(entidad.completadaEn)
    }

    @Test
    fun `ProgresoLeccionDto mejorXp mapea correctamente`() {
        val dto = progresoDtoBase.copy(mejorXp = 0)
        val entidad = dto.aEntidad()

        assertEquals(0, entidad.mejorXp)
    }

    @Test
    fun `ProgresoLeccionDto rol campo se preserva`() {
        val dto = progresoDtoBase.copy(rol = "peaton")
        val entidad = dto.aEntidad()

        assertEquals("peaton", entidad.rol)
    }
}
