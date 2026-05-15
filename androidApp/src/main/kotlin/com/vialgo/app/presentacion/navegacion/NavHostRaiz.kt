package com.vialgo.app.presentacion.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vialgo.app.presentacion.aprender.PantallaAprender
import com.vialgo.app.presentacion.autenticacion.PantallaInvitado
import com.vialgo.app.presentacion.autenticacion.PantallaLogin
import com.vialgo.app.presentacion.autenticacion.PantallaRecuperacion
import com.vialgo.app.presentacion.autenticacion.PantallaRegistro
import com.vialgo.app.presentacion.beneficios.PantallaBeneficios
import com.vialgo.app.presentacion.onboarding.PantallaBienvenida
import com.vialgo.app.presentacion.onboarding.PantallaCompromiso
import com.vialgo.app.presentacion.onboarding.PantallaSeleccionRol
import com.vialgo.app.presentacion.onboarding.PantallaTutorialIntro
import com.vialgo.app.presentacion.perfil.PantallaPerfil
import com.vialgo.app.presentacion.ranking.PantallaRanking
import com.vialgo.app.presentacion.sesion.PantallaClasificacion
import com.vialgo.app.presentacion.sesion.PantallaResultado
import com.vialgo.app.presentacion.sesion.PantallaSesion
import com.vialgo.app.presentacion.sesion.PantallaTutorial

private val rutasConBarraInferior = setOf(
    RutaAprender::class,
    RutaRanking::class,
    RutaBeneficios::class,
    RutaPerfil::class,
)

@Composable
fun NavHostRaiz() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()

    val mostrarBarra = entradaActual?.destination?.let { destino ->
        rutasConBarraInferior.any { destino.hasRoute(it) }
    } ?: false

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraNavegacionInferior(navController = navController)
            }
        },
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = GrafoAuth,
            modifier = Modifier.padding(paddingInterno),
        ) {
            // ─── Grafo Auth ───────────────────────────────────────────────
            navigation<GrafoAuth>(startDestination = RutaLogin) {
                composable<RutaLogin> {
                    PantallaLogin(navController = navController)
                }
                composable<RutaRegistro> {
                    PantallaRegistro(navController = navController)
                }
                composable<RutaRecuperacion> {
                    PantallaRecuperacion(navController = navController)
                }
                composable<RutaInvitado> {
                    PantallaInvitado(navController = navController)
                }
            }

            // ─── Grafo Principal (Bottom Bar) ─────────────────────────────
            navigation<GrafoPrincipal>(startDestination = RutaAprender) {
                composable<RutaAprender> {
                    PantallaAprender(navController = navController)
                }
                composable<RutaRanking> {
                    PantallaRanking(navController = navController)
                }
                composable<RutaBeneficios> {
                    PantallaBeneficios(navController = navController)
                }
                composable<RutaPerfil> {
                    PantallaPerfil(navController = navController)
                }
            }

            // ─── Grafo Sesión ─────────────────────────────────────────────
            navigation<GrafoSesion>(startDestination = RutaTutorial) {
                composable<RutaTutorial> {
                    PantallaTutorial(navController = navController)
                }
                composable<RutaClasificacion> {
                    PantallaClasificacion(navController = navController)
                }
                composable<RutaSesion> { entrada ->
                    val ruta = entrada.toRoute<RutaSesion>()
                    PantallaSesion(navController = navController, leccionId = ruta.leccionId)
                }
                composable<RutaResultado> { entrada ->
                    val ruta = entrada.toRoute<RutaResultado>()
                    PantallaResultado(navController = navController, sesionId = ruta.sesionId)
                }
            }

            // ─── Grafo Onboarding ─────────────────────────────────────────
            navigation<GrafoOnboarding>(startDestination = RutaBienvenida) {
                composable<RutaBienvenida> {
                    PantallaBienvenida(navController = navController)
                }
                composable<RutaSeleccionRol> {
                    PantallaSeleccionRol(navController = navController)
                }
                composable<RutaCompromiso> {
                    PantallaCompromiso(navController = navController)
                }
                composable<RutaTutorialIntro> {
                    PantallaTutorialIntro(navController = navController)
                }
            }
        }
    }
}
