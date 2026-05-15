package com.vialgo.app.di

import com.vialgo.app.fcm.FuenteTokenFcmImpl
import com.vialgo.app.plataforma.AlmacenamientoLocal
import com.vialgo.app.plataforma.FuenteTokenFcm
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val moduloPlataforma = module {
    single { AlmacenamientoLocal(context = androidContext()) }
    single<FuenteTokenFcm> { FuenteTokenFcmImpl() }
}
