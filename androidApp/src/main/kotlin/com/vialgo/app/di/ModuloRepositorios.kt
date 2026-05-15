package com.vialgo.app.di

import com.vialgo.app.datos.repositorios.RepositorioAutenticacionImpl
import com.vialgo.app.datos.repositorios.RepositorioContenidoImpl
import com.vialgo.app.datos.repositorios.RepositorioGamificacionImpl
import com.vialgo.app.datos.repositorios.RepositorioNotificacionesImpl
import com.vialgo.app.datos.repositorios.RepositorioSesionImpl
import com.vialgo.app.datos.repositorios.RepositorioUsuarioImpl
import com.vialgo.app.dominio.repositorios.RepositorioAutenticacion
import com.vialgo.app.dominio.repositorios.RepositorioContenido
import com.vialgo.app.dominio.repositorios.RepositorioGamificacion
import com.vialgo.app.dominio.repositorios.RepositorioNotificaciones
import com.vialgo.app.dominio.repositorios.RepositorioSesion
import com.vialgo.app.dominio.repositorios.RepositorioUsuario
import org.koin.dsl.module

val moduloRepositorios = module {
    single<RepositorioAutenticacion> { RepositorioAutenticacionImpl(cliente = get()) }
    single<RepositorioSesion> { RepositorioSesionImpl(cliente = get()) }
    single<RepositorioContenido> { RepositorioContenidoImpl(cliente = get()) }
    single<RepositorioUsuario> { RepositorioUsuarioImpl(cliente = get()) }
    single<RepositorioGamificacion> { RepositorioGamificacionImpl(cliente = get()) }
    single<RepositorioNotificaciones> { RepositorioNotificacionesImpl(cliente = get()) }
}
