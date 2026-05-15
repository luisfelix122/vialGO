package com.vialgo.app.dominio.comun

sealed class Resultado<out T> {
    data class Exito<out T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String, val causa: Throwable? = null) : Resultado<Nothing>()
    data object Cargando : Resultado<Nothing>()
}

inline fun <T> Resultado<T>.alExito(accion: (T) -> Unit): Resultado<T> {
    if (this is Resultado.Exito) accion(datos)
    return this
}

inline fun <T> Resultado<T>.alError(accion: (String, Throwable?) -> Unit): Resultado<T> {
    if (this is Resultado.Error) accion(mensaje, causa)
    return this
}

inline fun <T, R> Resultado<T>.mapear(transformar: (T) -> R): Resultado<R> = when (this) {
    is Resultado.Exito -> Resultado.Exito(transformar(datos))
    is Resultado.Error -> Resultado.Error(mensaje, causa)
    is Resultado.Cargando -> Resultado.Cargando
}
