package com.vialgo.app.datos.mapeadores

/**
 * Maps raw server/SDK error messages to user-friendly Spanish messages.
 * Keeps technical details out of the UI while still being informative.
 */
object ErrorMapeador {

    private val mappings = listOf(
        // Auth - Registration
        "DNI_ALREADY_EXISTS" to "Este DNI ya tiene una cuenta registrada. ¿Querés iniciar sesión?",
        "DNI already registered" to "Este DNI ya tiene una cuenta registrada. ¿Querés iniciar sesión?",
        "INVALID_DNI" to "El DNI debe tener exactamente 8 dígitos numéricos.",
        "INVALID_PASSWORD" to "La contraseña debe tener al menos 6 caracteres.",
        "INVALID_NOMBRE" to "El nombre es obligatorio.",
        "INVALID_PREGUNTA" to "Seleccioná una pregunta de seguridad.",
        "INVALID_RESPUESTA" to "La respuesta de seguridad es obligatoria.",
        "INVALID_ROL" to "Seleccioná un rol válido (conductor o peatón).",
        "INVALID_COMPROMISO" to "Seleccioná un tiempo de compromiso válido.",

        // Auth - Login
        "INVALID_CREDENTIALS" to "DNI o contraseña incorrectos. Verificá tus datos.",
        "USER_NOT_FOUND" to "No encontramos una cuenta con ese DNI.",
        "Invalid login credentials" to "DNI o contraseña incorrectos. Verificá tus datos.",

        // Auth - Anonymous / Guest
        "anonymous_provider_disabled" to "El modo invitado no está disponible en este momento. Registrate o iniciá sesión para explorar VialGo.",
        "Anonymous sign-ins are disabled" to "El modo invitado no está disponible en este momento. Registrate o iniciá sesión para explorar VialGo.",

        // Auth - Recovery
        "WRONG_ANSWER" to "La respuesta de seguridad no es correcta.",
        "PREGUNTA_NO_COINCIDE" to "La pregunta de seguridad no coincide.",

        // Network
        "Unable to resolve host" to "Sin conexión a internet. Verificá tu conexión e intentá de nuevo.",
        "timeout" to "La conexión tardó demasiado. Intentá de nuevo.",
        "SocketTimeoutException" to "La conexión tardó demasiado. Intentá de nuevo.",
        "UnknownHostException" to "Sin conexión a internet. Verificá tu conexión e intentá de nuevo.",
        "ConnectException" to "No se pudo conectar al servidor. Intentá de nuevo más tarde.",

        // Server errors
        "REGISTRATION_ERROR" to "Hubo un problema al crear tu cuenta. Intentá de nuevo.",
        "AUTH_CREATE_ERROR" to "Hubo un problema al crear tu cuenta. Intentá de nuevo.",
        "HASH_ERROR" to "Error interno del servidor. Intentá de nuevo más tarde.",
        "DB_ERROR" to "Error interno del servidor. Intentá de nuevo más tarde.",
        "SIGNIN_ERROR" to "Tu cuenta se creó pero hubo un error al iniciar sesión. Intentá iniciar sesión manualmente.",
    )

    /**
     * Translates a raw error message to a user-friendly Spanish message.
     * Checks if the raw message contains any known error pattern (case-insensitive).
     * Returns a friendly message if matched, or a generic fallback.
     */
    fun traducir(mensajeRaw: String?): String {
        if (mensajeRaw.isNullOrBlank()) return MENSAJE_GENERICO

        val mensajeLower = mensajeRaw.lowercase()
        for ((patron, amigable) in mappings) {
            if (mensajeLower.contains(patron.lowercase())) {
                return amigable
            }
        }

        return MENSAJE_GENERICO
    }

    private const val MENSAJE_GENERICO =
        "Ocurrió un error inesperado. Intentá de nuevo más tarde."
}
