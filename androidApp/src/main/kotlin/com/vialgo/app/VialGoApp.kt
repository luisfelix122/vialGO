package com.vialgo.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vialgo.app.di.moduloCasosDeUso
import com.vialgo.app.di.moduloPlataforma
import com.vialgo.app.di.moduloRed
import com.vialgo.app.di.moduloRepositorios
import com.vialgo.app.di.moduloViewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VialGoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        iniciarKoin()
        crearCanalNotificaciones()
    }

    private fun iniciarKoin() {
        startKoin {
            androidLogger()
            androidContext(this@VialGoApp)
            modules(
                moduloRed,
                moduloRepositorios,
                moduloCasosDeUso,
                moduloViewModels,
                moduloPlataforma,
            )
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_RECORDATORIO,
                "Recordatorio diario",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Recordatorios para completar tu lección diaria de seguridad vial"
            }
            val gestor = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            gestor.createNotificationChannel(canal)
        }
    }

    companion object {
        const val CANAL_RECORDATORIO = "recordatorio_diario"
    }
}
