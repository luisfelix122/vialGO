package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.UsuarioTablaDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.entidades.RolUsuario
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class UsuarioTablaMapeadorTest {

    private val dtoBase = UsuarioTablaDto(
        id = "tabla-usuario-001",
        dni = "87654321",
        nombre = "Maria Lopez",
        rolActivo = "conductor",
        compromisoMinutos = 15,
        tutorialCompletado = true,
        debeCambiarPregunta = false,
        fechaRegistro = "2024-03-01T10:00:00Z",
        actualizadoEn = "2024-06-15T08:30:00Z",
    )

    @Test
    fun `UsuarioTablaDto aEntidad mapea id correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals("tabla-usuario-001", entidad.id)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea dni correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals("87654321", entidad.dni)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea nombre correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals("Maria Lopez", entidad.nombre)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea rol conductor correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(RolUsuario.CONDUCTOR, entidad.rol)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea rol peaton correctamente`() {
        val dto = dtoBase.copy(rolActivo = "peaton")
        val entidad = dto.aEntidad()
        assertEquals(RolUsuario.PEATONAL, entidad.rol)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea compromisoMinutos correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(15, entidad.compromisoMinutos)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea tutorialCompletado correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(true, entidad.tutorialCompletado)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea debeCambiarPregunta correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(false, entidad.debeCambiarPregunta)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea fechaRegistro a creadoEn correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(Instant.parse("2024-03-01T10:00:00Z"), entidad.creadoEn)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea actualizadoEn correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(Instant.parse("2024-06-15T08:30:00Z"), entidad.actualizadoEn)
    }

    @Test
    fun `UsuarioTablaDto aEntidad con fechaRegistro en blanco usa epoch por defecto`() {
        val dto = dtoBase.copy(fechaRegistro = "")
        val entidad = dto.aEntidad()
        assertEquals(Instant.fromEpochMilliseconds(0), entidad.creadoEn)
    }

    @Test
    fun `UsuarioTablaDto aEntidad con actualizadoEn en blanco usa epoch por defecto`() {
        val dto = dtoBase.copy(actualizadoEn = "")
        val entidad = dto.aEntidad()
        assertEquals(Instant.fromEpochMilliseconds(0), entidad.actualizadoEn)
    }

    @Test
    fun `UsuarioTablaDto aEntidad inicializa campos de gamificacion en cero`() {
        val entidad = dtoBase.aEntidad()
        assertEquals(0, entidad.vidas)
        assertEquals(0, entidad.rachaActual)
        assertEquals(0, entidad.rachaMasLarga)
        assertEquals(0, entidad.puntosExperiencia)
        assertEquals(0, entidad.nivel)
        assertEquals("", entidad.correo)
    }

    @Test
    fun `UsuarioTablaDto aEntidad mapea rolActivo como string correctamente`() {
        val entidad = dtoBase.aEntidad()
        assertEquals("conductor", entidad.rolActivo)
    }
}
