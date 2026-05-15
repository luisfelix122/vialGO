package com.vialgo.app.plataforma

/**
 * Fuente del token FCM.
 * La implementación real se provee desde androidApp mediante inyección de dependencias.
 * Este stub permite que el shared module compile sin depender de firebase-messaging.
 */
interface FuenteTokenFcm {
    suspend fun obtenerToken(): String
    suspend fun eliminarToken()
}
