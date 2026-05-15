package com.vialgo.app.di

import com.vialgo.app.dominio.casosdeuso.autenticacion.CerrarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionInvitadoUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.IniciarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.RecuperarContrasenaUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.RegistrarUsuarioUseCase
import com.vialgo.app.dominio.casosdeuso.autenticacion.VerificarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.FinalizarSesionUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.IniciarSesionJuegoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerModulosUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerPreguntasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerProgresoUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ObtenerVidasUseCase
import com.vialgo.app.dominio.casosdeuso.sesion.ResponderPreguntaUseCase
import org.koin.dsl.module

val moduloCasosDeUso = module {
    // Authentication use cases
    factory { IniciarSesionUseCase(repositorio = get()) }
    factory { RegistrarUsuarioUseCase(repositorio = get()) }
    factory { RecuperarContrasenaUseCase(repositorio = get()) }
    factory { IniciarSesionInvitadoUseCase(repositorio = get()) }
    factory { CerrarSesionUseCase(repositorio = get()) }
    factory { VerificarSesionUseCase(repositorio = get()) }

    // Session use cases
    factory { ObtenerModulosUseCase(repositorio = get()) }
    factory { ObtenerPreguntasUseCase(repositorio = get()) }
    factory { IniciarSesionJuegoUseCase(repoSesion = get(), repoGamificacion = get()) }
    factory { ResponderPreguntaUseCase(repoSesion = get(), repoGamificacion = get()) }
    factory { FinalizarSesionUseCase(repositorio = get()) }
    factory { ObtenerVidasUseCase(repositorio = get()) }
    factory { ObtenerProgresoUseCase(repositorio = get()) }
}
