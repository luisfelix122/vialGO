package com.vialgo.app.di

import com.vialgo.app.BuildConfig
import com.vialgo.app.datos.fuentes.crearClienteSupabase
import org.koin.dsl.module

val moduloRed = module {
    single {
        crearClienteSupabase(
            url = BuildConfig.SUPABASE_URL,
            claveAnonima = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
}
