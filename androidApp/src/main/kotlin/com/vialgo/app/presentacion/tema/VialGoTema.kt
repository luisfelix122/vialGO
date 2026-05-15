package com.vialgo.app.presentacion.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaColoresOscuro = darkColorScheme(
    primary = VerdePrimario,
    onPrimary = TextoPrimario,
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = VerdeClaro,
    secondary = AmbarSecundario,
    onSecondary = FondoOscuro,
    secondaryContainer = AmbarOscuro,
    onSecondaryContainer = AmbarClaro,
    background = FondoOscuro,
    onBackground = TextoPrimario,
    surface = SuperficieOscura,
    onSurface = TextoPrimario,
    surfaceVariant = SuperficieElevada,
    onSurfaceVariant = TextoSecundario,
    error = Rojo,
    onError = FondoOscuro,
)

@Composable
fun VialGoTema(
    contenido: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = EsquemaColoresOscuro,
        typography = TipografiaVialGo,
        content = contenido,
    )
}
