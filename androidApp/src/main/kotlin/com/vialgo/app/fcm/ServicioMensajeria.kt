package com.vialgo.app.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.vialgo.app.ActividadPrincipal
import com.vialgo.app.VialGoApp
class ServicioMensajeria : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token is cached locally; binding to a user ID happens on next authenticated session.
        Log.d("ServicioMensajeria", "Nuevo token FCM recibido")
    }

    override fun onMessageReceived(mensaje: RemoteMessage) {
        super.onMessageReceived(mensaje)
        val titulo = mensaje.notification?.title ?: return
        val cuerpo = mensaje.notification?.body ?: return
        mostrarNotificacion(titulo, cuerpo)
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val intent = Intent(this, ActividadPrincipal::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val intentPendiente = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notificacion = NotificationCompat.Builder(this, VialGoApp.CANAL_RECORDATORIO)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(intentPendiente)
            .build()

        val gestor = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        gestor.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}
