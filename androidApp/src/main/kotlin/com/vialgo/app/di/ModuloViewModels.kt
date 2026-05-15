package com.vialgo.app.di

import com.vialgo.app.presentacion.aprender.AprenderViewModel
import com.vialgo.app.presentacion.autenticacion.InvitadoViewModel
import com.vialgo.app.presentacion.autenticacion.LoginViewModel
import com.vialgo.app.presentacion.autenticacion.RecuperacionViewModel
import com.vialgo.app.presentacion.autenticacion.RegistroViewModel
import com.vialgo.app.presentacion.sesion.ResultadoViewModel
import com.vialgo.app.presentacion.sesion.SesionViewModel
import org.koin.dsl.module

val moduloViewModels = module {
    // Authentication ViewModels
    factory { LoginViewModel(iniciarSesion = get()) }
    factory { RegistroViewModel(registrarUsuario = get()) }
    factory { RecuperacionViewModel(recuperarContrasena = get()) }
    factory { InvitadoViewModel(iniciarSesionInvitado = get()) }

    // Session ViewModels
    factory {
        AprenderViewModel(
            obtenerModulos = get(),
            obtenerProgreso = get(),
            obtenerVidas = get(),
        )
    }
    factory {
        SesionViewModel(
            iniciarSesionJuego = get(),
            obtenerPreguntas = get(),
            responderPregunta = get(),
            finalizarSesion = get(),
        )
    }
    factory { ResultadoViewModel(repositorioSesion = get()) }
}
