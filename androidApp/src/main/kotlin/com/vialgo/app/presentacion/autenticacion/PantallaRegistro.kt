package com.vialgo.app.presentacion.autenticacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.draw.clip
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
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import com.vialgo.app.presentacion.tema.SuperficieElevada
import com.vialgo.app.presentacion.tema.SuperficieOscura
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import com.vialgo.app.presentacion.tema.VerdePrimario
import com.vialgo.app.presentacion.tema.VerdeClaro
import org.koin.compose.getKoin

private val preguntasSeguridad = listOf(
    "¿Nombre de tu primera mascota?",
    "¿Ciudad donde naciste?",
    "¿Nombre de tu mejor amigo/a?",
)

private val opcionesCompromiso = listOf(5, 10, 15, 20, 30)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaRegistro(navController: NavController) {
    val koin = getKoin()
    val viewModel = remember { koin.get<RegistroViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()
    var contrasenaVisible by remember { mutableStateOf(false) }
    var expandidoPregunta by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoNavegacion.IrAOnboarding -> navController.navigate(GrafoOnboarding) {
                    popUpTo(GrafoAuth) { inclusive = true }
                }
                EventoNavegacion.IrAPrincipal -> navController.navigate(GrafoAuth) {
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
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = estado.mensaje,
                        color = Rojo,
                        modifier = Modifier.padding(24.dp),
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
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Crear cuenta",
                        color = TextoPrimario,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // DNI
                    OutlinedTextField(
                        value = datos.dni,
                        onValueChange = { if (it.length <= 8) viewModel.onDniCambiado(it) },
                        label = { Text("DNI", color = TextoSecundario) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = camposColores(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre
                    OutlinedTextField(
                        value = datos.nombre,
                        onValueChange = { viewModel.onNombreCambiado(it) },
                        label = { Text("Nombre completo", color = TextoSecundario) },
                        singleLine = true,
                        colors = camposColores(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contraseña
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
                        colors = camposColores(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pregunta de seguridad (dropdown)
                    ExposedDropdownMenuBox(
                        expanded = expandidoPregunta,
                        onExpandedChange = { expandidoPregunta = it },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            value = datos.preguntaSeguridad.ifEmpty { "Seleccionar pregunta" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pregunta de seguridad", color = TextoSecundario) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Expandir",
                                    tint = TextoSecundario,
                                )
                            },
                            colors = camposColores(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                        )
                        ExposedDropdownMenu(
                            expanded = expandidoPregunta,
                            onDismissRequest = { expandidoPregunta = false },
                            modifier = Modifier.background(SuperficieOscura),
                        ) {
                            preguntasSeguridad.forEach { pregunta ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = pregunta,
                                            color = TextoPrimario,
                                            fontSize = 14.sp,
                                        )
                                    },
                                    onClick = {
                                        viewModel.onPreguntaSeguridadCambiada(pregunta)
                                        expandidoPregunta = false
                                    },
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Respuesta de seguridad
                    OutlinedTextField(
                        value = datos.respuestaSeguridad,
                        onValueChange = { viewModel.onRespuestaSeguridadCambiada(it) },
                        label = { Text("Respuesta de seguridad", color = TextoSecundario) },
                        singleLine = true,
                        colors = camposColores(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Selector de rol
                    Text(
                        text = "Soy...",
                        color = TextoSecundario,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        listOf("conductor" to "Conductor", "peaton" to "Peatón").forEach { (valor, etiqueta) ->
                            val seleccionado = datos.rolActivo == valor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (seleccionado) VerdePrimario else SuperficieElevada)
                                    .border(
                                        width = 1.dp,
                                        color = if (seleccionado) VerdeClaro else TextoSecundario,
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .clickable { viewModel.onRolActivoCambiado(valor) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = etiqueta,
                                    color = if (seleccionado) TextoPrimario else TextoSecundario,
                                    fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Selector de compromiso
                    Text(
                        text = "Compromiso diario (minutos)",
                        color = TextoSecundario,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        opcionesCompromiso.forEach { minutos ->
                            val seleccionado = datos.compromisoMinutos == minutos
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (seleccionado) VerdePrimario else SuperficieElevada)
                                    .border(
                                        width = 1.dp,
                                        color = if (seleccionado) VerdeClaro else TextoSecundario,
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .clickable { viewModel.onCompromisoMinutosCambiado(minutos) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "$minutos min",
                                    color = if (seleccionado) TextoPrimario else TextoSecundario,
                                    fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }

                    if (datos.errorGeneral != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = datos.errorGeneral,
                            color = Rojo,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.onRegistrar() },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Registrarse",
                            color = TextoPrimario,
                            fontSize = 16.sp,
                        )
                    }

                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "Ya tengo cuenta",
                            color = VerdeClaro,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun camposColores() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextoPrimario,
    unfocusedTextColor = TextoPrimario,
    focusedBorderColor = VerdeClaro,
    unfocusedBorderColor = TextoSecundario,
    cursorColor = VerdeClaro,
)
