package com.vialgo.app.presentacion.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.casosdeuso.autenticacion.CerrarSesionUseCase
import com.vialgo.app.dominio.comun.Resultado
import com.vialgo.app.presentacion.navegacion.GrafoAuth
import com.vialgo.app.presentacion.navegacion.GrafoPrincipal
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun PantallaPerfil(navController: NavController) {
    val koin = getKoin()
    val cerrarSesion = remember { koin.get<CerrarSesionUseCase>() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Perfil",
                    color = TextoPrimario,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aquí verás tu información de perfil.",
                    color = TextoSecundario,
                    fontSize = 15.sp,
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        val resultado = cerrarSesion.ejecutar()
                        if (resultado is Resultado.Exito) {
                            navController.navigate(GrafoAuth) {
                                popUpTo(GrafoPrincipal) { inclusive = true }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Rojo),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            ) {
                Text(
                    text = "Cerrar sesión",
                    color = TextoPrimario,
                    fontSize = 16.sp,
                )
            }
        }
    }
}
