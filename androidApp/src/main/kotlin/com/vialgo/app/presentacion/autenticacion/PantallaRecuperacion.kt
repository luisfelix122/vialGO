package com.vialgo.app.presentacion.autenticacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import androidx.compose.foundation.shape.RoundedCornerShape
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import com.vialgo.app.presentacion.tema.VerdePrimario
import com.vialgo.app.presentacion.tema.VerdeClaro
import org.koin.compose.getKoin

@Composable
fun PantallaRecuperacion(navController: NavController) {
    val koin = getKoin()
    val viewModel = remember { koin.get<RecuperacionViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()
    var contrasenaVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoNavegacion.Volver -> navController.popBackStack()
                EventoNavegacion.IrAPrincipal -> navController.popBackStack()
                EventoNavegacion.IrAOnboarding -> navController.popBackStack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro),
    ) {
        when (val estado = estadoUi) {
            is EstadoUi.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = VerdeClaro)
                }
            }
            is EstadoUi.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = estado.mensaje,
                        color = Rojo,
                    )
                }
            }
            is EstadoUi.Contenido -> {
                val datos = estado.datos
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Recuperar contraseña",
                        color = TextoPrimario,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ingresá tu DNI, respondé la pregunta de seguridad y establecé una nueva contraseña.",
                        color = TextoSecundario,
                        fontSize = 14.sp,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (datos.exito) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = VerdePrimario.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .padding(16.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = VerdeClaro,
                                    modifier = Modifier.size(28.dp),
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Column {
                                    Text(
                                        text = "¡Contraseña actualizada!",
                                        color = VerdeClaro,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "Volviendo al inicio de sesión...",
                                        color = TextoSecundario,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = datos.dni,
                        onValueChange = { if (it.length <= 8) viewModel.onDniCambiado(it) },
                        label = { Text("DNI", color = TextoSecundario) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = VerdeClaro,
                            unfocusedBorderColor = TextoSecundario,
                            cursorColor = VerdeClaro,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = datos.respuestaSeguridad,
                        onValueChange = { viewModel.onRespuestaSeguridadCambiada(it) },
                        label = { Text("Respuesta de seguridad", color = TextoSecundario) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = VerdeClaro,
                            unfocusedBorderColor = TextoSecundario,
                            cursorColor = VerdeClaro,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = datos.nuevaContrasena,
                        onValueChange = { viewModel.onNuevaContrasenaCambiada(it) },
                        label = { Text("Nueva contraseña", color = TextoSecundario) },
                        visualTransformation = if (contrasenaVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                                Icon(
                                    imageVector = if (contrasenaVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (contrasenaVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = TextoSecundario,
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = VerdeClaro,
                            unfocusedBorderColor = TextoSecundario,
                            cursorColor = VerdeClaro,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (datos.errorGeneral != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Rojo.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .padding(14.dp),
                        ) {
                            Text(
                                text = datos.errorGeneral,
                                color = Rojo,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.onRecuperar() },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Recuperar contraseña",
                            color = TextoPrimario,
                            fontSize = 16.sp,
                        )
                    }

                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "Volver",
                            color = TextoSecundario,
                        )
                    }
                }
            }
        }
    }
}
