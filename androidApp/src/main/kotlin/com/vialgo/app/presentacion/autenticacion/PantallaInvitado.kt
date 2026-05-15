package com.vialgo.app.presentacion.autenticacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.presentacion.navegacion.GrafoAuth
import com.vialgo.app.presentacion.navegacion.GrafoPrincipal
import com.vialgo.app.presentacion.navegacion.RutaRegistro
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import com.vialgo.app.presentacion.tema.SuperficieOscura
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import com.vialgo.app.presentacion.tema.VerdePrimario
import com.vialgo.app.presentacion.tema.VerdeClaro
import org.koin.compose.getKoin

@Composable
fun PantallaInvitado(navController: NavController) {
    val koin = getKoin()
    val viewModel = remember { koin.get<InvitadoViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoNavegacion.IrAPrincipal -> navController.navigate(GrafoPrincipal) {
                    popUpTo(GrafoAuth) { inclusive = true }
                }
                EventoNavegacion.IrAOnboarding -> navController.navigate(GrafoAuth) {
                    popUpTo(GrafoAuth) { inclusive = true }
                }
                EventoNavegacion.Volver -> navController.popBackStack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro),
        contentAlignment = Alignment.Center,
    ) {
        when (val estado = estadoUi) {
            is EstadoUi.Cargando -> {
                CircularProgressIndicator(color = VerdeClaro)
            }
            is EstadoUi.Error -> {
                Text(
                    text = estado.mensaje,
                    color = Rojo,
                    modifier = Modifier.padding(24.dp),
                )
            }
            is EstadoUi.Contenido -> {
                val datos = estado.datos

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Modo invitado",
                        color = TextoPrimario,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Podés explorar VialGo sin crear una cuenta. Recordá que sin cuenta no podrás guardar tu progreso.",
                        color = TextoSecundario,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )

                    if (datos.errorGeneral != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = datos.errorGeneral,
                            color = Rojo,
                            fontSize = 14.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { viewModel.onIniciarComoInvitado() },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Continuar como invitado",
                            color = TextoPrimario,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "Volver",
                            color = TextoSecundario,
                        )
                    }
                }

                // Diálogo para prompt de registro
                if (datos.mostrarPromptRegistro) {
                    AlertDialog(
                        onDismissRequest = { viewModel.onDescartarPrompt() },
                        containerColor = SuperficieOscura,
                        title = {
                            Text(
                                text = "¿Querés guardar tu progreso?",
                                color = TextoPrimario,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        text = {
                            Text(
                                text = "Creá una cuenta para no perder tu avance y competir en el ranking.",
                                color = TextoSecundario,
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.onDescartarPrompt()
                                    navController.navigate(RutaRegistro)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                            ) {
                                Text(
                                    text = "Registrarse",
                                    color = TextoPrimario,
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.onDescartarPrompt() }) {
                                Text(
                                    text = "Seguir como invitado",
                                    color = VerdeClaro,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
