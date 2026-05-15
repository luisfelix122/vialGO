package com.vialgo.app.dominio.casosdeuso

import com.vialgo.app.dominio.comun.Resultado

/**
 * Base para casos de uso asincrónicos.
 *
 * @param P tipo de parámetros de entrada
 * @param R tipo del resultado exitoso
 */
interface CasoDeUsoBase<in P, out R> {
    suspend fun ejecutar(parametros: P): Resultado<R>
}

/**
 * Base para casos de uso sin parámetros.
 */
interface CasoDeUsoSinParametros<out R> {
    suspend fun ejecutar(): Resultado<R>
}

/**
 * Objeto vacío para casos de uso sin parámetros que usan CasoDeUsoBase.
 */
object SinParametros
