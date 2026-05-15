package com.vialgo.app.mapeadores

import com.vialgo.app.datos.dtos.OpcionPreguntaDto
import com.vialgo.app.datos.dtos.PreguntaDto
import com.vialgo.app.datos.mapeadores.aEntidad
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContenidoMapeadorTest {

    private val preguntaDtoBase = PreguntaDto(
        id = "preg-001",
        leccionId = "leccion-123",
        enunciado = "¿Cuál es la velocidad máxima en zona urbana?",
        tipo = "opcion_multiple",
        urlImagen = null,
        urlVideo = null,
        orden = 1,
    )

    private val opcionesDtos = listOf(
        OpcionPreguntaDto(
            id = "opc-001",
            preguntaId = "preg-001",
            texto = "40 km/h",
            esCorrecta = false,
            orden = 1,
        ),
        OpcionPreguntaDto(
            id = "opc-002",
            preguntaId = "preg-001",
            texto = "60 km/h",
            esCorrecta = true,
            orden = 2,
        ),
    )

    @Test
    fun `PreguntaDto aEntidad con leccionId no nulo mapea correctamente`() {
        val opciones = opcionesDtos.map { it.aEntidad() }
        val entidad = preguntaDtoBase.aEntidad(opciones)

        assertEquals("preg-001", entidad.id)
        assertEquals("leccion-123", entidad.leccionId)
        assertEquals("¿Cuál es la velocidad máxima en zona urbana?", entidad.enunciado)
        assertEquals(1, entidad.orden)
        assertEquals(2, entidad.opciones.size)
    }

    @Test
    fun `PreguntaDto aEntidad con leccionId null mapea correctamente`() {
        val dto = preguntaDtoBase.copy(leccionId = null)
        val entidad = dto.aEntidad()

        assertNull(entidad.leccionId)
    }

    @Test
    fun `PreguntaDto aEntidad con lista de opciones vacia`() {
        val entidad = preguntaDtoBase.aEntidad(emptyList())

        assertEquals(0, entidad.opciones.size)
    }

    @Test
    fun `OpcionPreguntaDto aEntidad mapea esCorrecta correctamente`() {
        val opcionCorrecta = opcionesDtos[1].aEntidad()
        val opcionIncorrecta = opcionesDtos[0].aEntidad()

        assertEquals(true, opcionCorrecta.esCorrecta)
        assertEquals(false, opcionIncorrecta.esCorrecta)
    }

    @Test
    fun `PreguntaDto aEntidad ensambla opciones por preguntaId`() {
        val opciones = opcionesDtos.map { it.aEntidad() }
        val entidad = preguntaDtoBase.aEntidad(opciones)

        assertEquals(2, entidad.opciones.size)
        assertEquals("opc-001", entidad.opciones[0].id)
        assertEquals("opc-002", entidad.opciones[1].id)
    }

    @Test
    fun `PreguntaDto aEntidad tipo opcion_multiple mapea a TipoPregunta`() {
        val entidad = preguntaDtoBase.aEntidad()

        assertEquals(
            com.vialgo.app.dominio.entidades.TipoPregunta.OPCION_MULTIPLE,
            entidad.tipo,
        )
    }
}
