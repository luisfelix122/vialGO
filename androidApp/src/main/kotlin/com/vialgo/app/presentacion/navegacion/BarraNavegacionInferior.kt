package com.vialgo.app.presentacion.navegacion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState

data class ItemNavInferior(
    val etiqueta: String,
    val icono: androidx.compose.ui.graphics.vector.ImageVector,
    val ruta: Any,
)

@Composable
fun BarraNavegacionInferior(navController: NavController) {
    val items = listOf(
        ItemNavInferior("Aprender", Icons.AutoMirrored.Filled.MenuBook, RutaAprender),
        ItemNavInferior("Ranking", Icons.Filled.EmojiEvents, RutaRanking),
        ItemNavInferior("Beneficios", Icons.Filled.LocalOffer, RutaBeneficios),
        ItemNavInferior("Perfil", Icons.Filled.Person, RutaPerfil),
    )

    val entradaActual by navController.currentBackStackEntryAsState()

    NavigationBar {
        items.forEach { item ->
            val seleccionado = entradaActual?.destination?.hasRoute(item.ruta::class) == true
            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    navController.navigate(item.ruta) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icono, contentDescription = item.etiqueta) },
                label = { Text(text = item.etiqueta) },
            )
        }
    }
}
