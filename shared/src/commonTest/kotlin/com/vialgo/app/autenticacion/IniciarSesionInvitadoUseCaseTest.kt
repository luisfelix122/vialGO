package com.vialgo.app.autenticacion

import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionInvitadoUseCase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.dominio.entidades.RolUsuario
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IniciarSesionInvitadoUseCaseTest {

    private val repositorio = FakeRepositorioAutenticacion()
    private val casoDeUso = IniciarSesionInvitadoUseCase(repositorio)

    @Test
    fun `sesion de invitado creada correctamente`() = runTest {
        val invitado = FakeRepositorioAutenticacion.usuarioInvitadoPrueba()
        repositorio.resultadoInvitado = Resultado.Exito(invitado)

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Exito<*>>(resultado)
        val usuario = (resultado as Resultado.Exito).datos
        assertEquals(RolUsuario.INVITADO, usuario.rol)
        assertEquals(1, repositorio.llamadasInvitado)
    }

    @Test
    fun `error en sesion de invitado se propaga`() = runTest {
        repositorio.resultadoInvitado = Resultado.Error("Error de red")

        val resultado = casoDeUso.ejecutar()

        assertIs<Resultado.Error>(resultado)
        assertEquals("Error de red", resultado.mensaje)
    }
}
