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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.presentacion.navegacion.GrafoAuth
import com.vialgo.app.presentacion.navegacion.GrafoOnboarding
import com.vialgo.app.presentacion.navegacion.GrafoPrincipal
import com.vialgo.app.presentacion.navegacion.RutaInvitado
import com.vialgo.app.presentacion.navegacion.RutaRecuperacion
import com.vialgo.app.presentacion.navegacion.RutaRegistro
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import com.vialgo.app.presentacion.tema.VerdePrimario
import com.vialgo.app.presentacion.tema.VerdeClaro
import org.koin.compose.getKoin

@Composable
fun PantallaLogin(navController: NavController) {
    val koin = getKoin()
    val viewModel = remember { koin.get<LoginViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()
    var contrasenaVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoNavegacion.IrAPrincipal -> navController.navigate(GrafoPrincipal) {
                    popUpTo(GrafoAuth) { inclusive = true }
                }
                EventoNavegacion.IrAOnboarding -> navController.navigate(GrafoOnboarding) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = estado.mensaje,
                        color = Rojo,
                        fontSize = 14.sp,
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
                        text = "VialGo",
                        color = VerdeClaro,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    OutlinedTextField(
                        value = datos.dni,
                        onValueChange = { valor ->
                            if (valor.length <= 8) viewModel.onDniCambiado(valor)
                        },
                        label = { Text("DNI", color = TextoSecundario) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = datos.errorDni != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = VerdeClaro,
                            unfocusedBorderColor = TextoSecundario,
                            cursorColor = VerdeClaro,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (datos.errorDni != null) {
                        Text(
                            text = datos.errorDni,
                            color = Rojo,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, top = 2.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = datos.contrasena,
                        onValueChange = { viewModel.onContrasenaCambiada(it) },
                        label = { Text("Contraseña", color = TextoSecundario) },
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
                        isError = datos.errorContrasena != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = VerdeClaro,
                            unfocusedBorderColor = TextoSecundario,
                            cursorColor = VerdeClaro,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (datos.errorContrasena != null) {
                        Text(
                            text = datos.errorContrasena,
                            color = Rojo,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, top = 2.dp),
                        )
                    }

                    if (datos.errorGeneral != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = datos.errorGeneral,
                            color = Rojo,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.onIniciarSesion() },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            color = TextoPrimario,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate(RutaRecuperacion) }) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = VerdeClaro,
                        )
                    }

                    TextButton(onClick = { navController.navigate(RutaRegistro) }) {
                        Text(
                            text = "Crear cuenta",
                            color = VerdeClaro,
                        )
                    }

                    TextButton(onClick = { navController.navigate(RutaInvitado) }) {
                        Text(
                            text = "Continuar como invitado",
                            color = TextoSecundario,
                        )
                    }
                }
            }
        }
    }
}
