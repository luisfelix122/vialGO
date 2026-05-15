package com.vialgo.app.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.vialgo.app.plataforma.FuenteTokenFcm
import kotlinx.coroutines.tasks.await

/**
 * Implementación concreta de FuenteTokenFcm usando Firebase Messaging.
 * Vive en androidApp para evitar la dependencia de firebase-messaging en el módulo shared.
 */
class FuenteTokenFcmImpl : FuenteTokenFcm {

    override suspend fun obtenerToken(): String =
        FirebaseMessaging.getInstance().token.await()

    override suspend fun eliminarToken() {
        FirebaseMessaging.getInstance().deleteToken().await()
    }
}
