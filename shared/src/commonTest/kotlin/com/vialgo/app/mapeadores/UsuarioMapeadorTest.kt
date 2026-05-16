package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.UsuarioAuthDto
import com.vialgo.app.datos.dtos.UsuarioDto
import com.vialgo.app.datos.mapeadores.aDto
import com.vialgo.app.datos.mapeadores.aEntidad
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.dominio.entidades.Usuario
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UsuarioMapeadorTest {

    // ---- UsuarioDto.aEntidad() ----

    private val usuarioDtoBase = UsuarioDto(
        id = "usuario-001",
        correo = "juan@example.com",
        nombre = "Juan Perez",
        rol = "conductor",
        vidas = 5,
        rachaActual = 3,
        rachaMasLarga = 10,
        puntosExperiencia = 750,
        nivel = 2,
        creadoEn = "2024-01-01T00:00:00Z",
        actualizadoEn = "2024-06-01T12:00:00Z",
        dni = "12345678",
        preguntaSeguridad = "Nombre de tu mascota",
        rolActivo = "conductor",
        compromisoMinutos = 30,
        tutorialCompletado = true,
        debeCambiarPregunta = false,
    )

    @Test
    fun `UsuarioDto aEntidad mapea todos los campos correctamente`() {
        val entidad = usuarioDtoBase.aEntidad()

        assertEquals("usuario-001", entidad.id)
        assertEquals("juan@example.com", entidad.correo)
        assertEquals("Juan Perez", entidad.nombre)
        assertEquals(RolUsuario.CONDUCTOR, entidad.rol)
        assertEquals(5, entidad.vidas)
        assertEquals(3, entidad.rachaActual)
        assertEquals(10, entidad.rachaMasLarga)
        assertEquals(750, entidad.puntosExperiencia)
        assertEquals(2, entidad.nivel)
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), entidad.creadoEn)
        assertEquals(Instant.parse("2024-06-01T12:00:00Z"), entidad.actualizadoEn)
        assertEquals("12345678", entidad.dni)
        assertEquals("Nombre de tu mascota", entidad.preguntaSeguridad)
        assertEquals("conductor", entidad.rolActivo)
        assertEquals(30, entidad.compromisoMinutos)
        assertEquals(true, entidad.tutorialCompletado)
        assertEquals(false, entidad.debeCambiarPregunta)
    }

    @Test
    fun `UsuarioDto aEntidad mapea rol peaton correctamente`() {
        val dto = usuarioDtoBase.copy(rol = "peaton")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.PEATONAL, entidad.rol)
    }

    @Test
    fun `UsuarioDto aEntidad mapea rol ciclista correctamente`() {
        val dto = usuarioDtoBase.copy(rol = "ciclista")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.CICLISTA, entidad.rol)
    }

    @Test
    fun `UsuarioDto aEntidad con rol en blanco usa CONDUCTOR por defecto`() {
        val dto = usuarioDtoBase.copy(rol = "")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.CONDUCTOR, entidad.rol)
    }

    @Test
    fun `UsuarioDto aEntidad con creadoEn en blanco usa epoch por defecto`() {
        val dto = usuarioDtoBase.copy(creadoEn = "")
        val entidad = dto.aEntidad()

        assertEquals(Instant.fromEpochMilliseconds(0), entidad.creadoEn)
    }

    @Test
    fun `UsuarioDto aEntidad con actualizadoEn en blanco usa epoch por defecto`() {
        val dto = usuarioDtoBase.copy(actualizadoEn = "")
        val entidad = dto.aEntidad()

        assertEquals(Instant.fromEpochMilliseconds(0), entidad.actualizadoEn)
    }

    @Test
    fun `UsuarioDto aEntidad con preguntaSeguridad null mapea a null`() {
        val dto = usuarioDtoBase.copy(preguntaSeguridad = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.preguntaSeguridad)
    }

    // ---- Usuario.aDto() ----

    private val usuarioBase = Usuario(
        id = "usuario-001",
        correo = "juan@example.com",
        nombre = "Juan Perez",
        rol = RolUsuario.CONDUCTOR,
        vidas = 5,
        rachaActual = 3,
        rachaMasLarga = 10,
        puntosExperiencia = 750,
        nivel = 2,
        creadoEn = Instant.parse("2024-01-01T00:00:00Z"),
        actualizadoEn = Instant.parse("2024-06-01T12:00:00Z"),
        dni = "12345678",
        preguntaSeguridad = "Nombre de tu mascota",
        rolActivo = "conductor",
        compromisoMinutos = 30,
        tutorialCompletado = true,
        debeCambiarPregunta = false,
    )

    @Test
    fun `Usuario aDto mapea todos los campos correctamente`() {
        val dto = usuarioBase.aDto()

        assertEquals("usuario-001", dto.id)
        assertEquals("juan@example.com", dto.correo)
        assertEquals("Juan Perez", dto.nombre)
        assertEquals("conductor", dto.rol)
        assertEquals(5, dto.vidas)
        assertEquals(3, dto.rachaActual)
        assertEquals(10, dto.rachaMasLarga)
        assertEquals(750, dto.puntosExperiencia)
        assertEquals(2, dto.nivel)
        assertEquals("12345678", dto.dni)
        assertEquals("Nombre de tu mascota", dto.preguntaSeguridad)
        assertEquals("conductor", dto.rolActivo)
        assertEquals(30, dto.compromisoMinutos)
        assertEquals(true, dto.tutorialCompletado)
        assertEquals(false, dto.debeCambiarPregunta)
    }

    @Test
    fun `Usuario aDto convierte rol PEATONAL a string en minuscula`() {
        val entidad = usuarioBase.copy(rol = RolUsuario.PEATONAL)
        val dto = entidad.aDto()

        assertEquals("peatonal", dto.rol)
    }

    @Test
    fun `Usuario aDto convierte rol CICLISTA a string en minuscula`() {
        val entidad = usuarioBase.copy(rol = RolUsuario.CICLISTA)
        val dto = entidad.aDto()

        assertEquals("ciclista", dto.rol)
    }

    // ---- UsuarioAuthDto.aEntidad() ----

    private val usuarioAuthDtoBase = UsuarioAuthDto(
        id = "usuario-auth-001",
        dni = "87654321",
        nombre = "Ana Garcia",
        rolActivo = "conductor",
        compromisoMinutos = 45,
        tutorialCompletado = false,
        debeCambiarPregunta = true,
    )

    @Test
    fun `UsuarioAuthDto aEntidad mapea campos de identidad correctamente`() {
        val entidad = usuarioAuthDtoBase.aEntidad()

        assertEquals("usuario-auth-001", entidad.id)
        assertEquals("87654321", entidad.dni)
        assertEquals("Ana Garcia", entidad.nombre)
        assertEquals("conductor", entidad.rolActivo)
        assertEquals(45, entidad.compromisoMinutos)
        assertEquals(false, entidad.tutorialCompletado)
        assertEquals(true, entidad.debeCambiarPregunta)
    }

    @Test
    fun `UsuarioAuthDto aEntidad inicializa campos de gamificacion en cero`() {
        val entidad = usuarioAuthDtoBase.aEntidad()

        assertEquals(0, entidad.vidas)
        assertEquals(0, entidad.rachaActual)
        assertEquals(0, entidad.rachaMasLarga)
        assertEquals(0, entidad.puntosExperiencia)
        assertEquals(0, entidad.nivel)
        assertEquals("", entidad.correo)
        assertEquals(Instant.fromEpochMilliseconds(0), entidad.creadoEn)
        assertEquals(Instant.fromEpochMilliseconds(0), entidad.actualizadoEn)
    }

    @Test
    fun `UsuarioAuthDto aEntidad mapea rolActivo conductor a RolUsuario CONDUCTOR`() {
        val entidad = usuarioAuthDtoBase.aEntidad()

        assertEquals(RolUsuario.CONDUCTOR, entidad.rol)
    }

    @Test
    fun `UsuarioAuthDto aEntidad con rol invalido usa CONDUCTOR por defecto`() {
        val dto = usuarioAuthDtoBase.copy(rolActivo = "rol_invalido")
        val entidad = dto.aEntidad()

        assertEquals(RolUsuario.CONDUCTOR, entidad.rol)
    }
}
