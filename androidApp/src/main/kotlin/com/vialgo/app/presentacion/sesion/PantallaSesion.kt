package com.vialgo.app.presentacion.sesion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.entidades.OpcionPregunta
import com.vialgo.app.dominio.entidades.Pregunta
import com.vialgo.app.presentacion.navegacion.GrafoPrincipal
import com.vialgo.app.presentacion.navegacion.GrafoSesion
import com.vialgo.app.presentacion.navegacion.RutaResultado
import com.vialgo.app.presentacion.tema.AmbarSecundario
import com.vialgo.app.presentacion.tema.FondoOscuro
import com.vialgo.app.presentacion.tema.Rojo
import com.vialgo.app.presentacion.tema.SuperficieElevada
import com.vialgo.app.presentacion.tema.SuperficieOscura
import com.vialgo.app.presentacion.tema.TextoPrimario
import com.vialgo.app.presentacion.tema.TextoSecundario
import com.vialgo.app.presentacion.tema.VerdePrimario
import com.vialgo.app.presentacion.tema.VerdeClaro
import org.koin.compose.getKoin

// ─── PantallaSesion ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSesion(navController: NavController, leccionId: String) {
    val koin = getKoin()
    val viewModel = remember { koin.get<SesionViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(leccionId) {
        viewModel.iniciar(usuarioId = "test-user", leccionId = leccionId, rol = "conductor")
    }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is EventoSesion.IrAResultado -> navController.navigate(RutaResultado(evento.sesionId))
                is EventoSesion.Salir -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.salir() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Salir",
                            tint = TextoPrimario,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperficieOscura),
            )
        },
        containerColor = FondoOscuro,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
                        fontSize = 14.sp,
                        modifier = Modifier.padding(24.dp),
                    )
                }
                is EstadoUi.Contenido -> {
                    val datos = estado.datos
                    val preguntaActual = datos.preguntas.getOrNull(datos.indicePreguntaActual)
                    if (preguntaActual != null) {
                        ContenidoSesion(
                            datos = datos,
                            preguntaActual = preguntaActual,
                            onSeleccionarOpcion = { opcionId -> viewModel.seleccionarOpcion(opcionId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContenidoSesion(
    datos: EstadoSesionJuego,
    preguntaActual: Pregunta,
    onSeleccionarOpcion: (String) -> Unit,
) {
    val totalPreguntas = datos.preguntas.size
    val indicePregunta = datos.indicePreguntaActual
    val progresoPregunta = if (totalPreguntas > 0) {
        (indicePregunta + 1).toFloat() / totalPreguntas.toFloat()
    } else 0f
    val progresoTiempo = datos.tiempoRestanteMs / 5000f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Progress bar (pregunta X/total)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Pregunta ${indicePregunta + 1} de $totalPreguntas",
                    color = TextoSecundario,
                    fontSize = 12.sp,
                )
                Text(
                    text = "XP: ${datos.xpAcumulado}",
                    color = AmbarSecundario,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progresoPregunta },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = VerdeClaro,
                trackColor = SuperficieElevada,
            )
        }

        // Timer
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progresoTiempo.coerceIn(0f, 1f) },
                modifier = Modifier.size(56.dp),
                color = when {
                    progresoTiempo > 0.5f -> VerdeClaro
                    progresoTiempo > 0.25f -> AmbarSecundario
                    else -> Rojo
                },
                trackColor = SuperficieElevada,
                strokeWidth = 4.dp,
            )
            Text(
                text = "${(datos.tiempoRestanteMs / 1000L)}s",
                color = TextoPrimario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = preguntaActual.enunciado,
                    color = TextoPrimario,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                )
                if (preguntaActual.urlImagen != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[Imagen disponible]",
                        color = TextoSecundario,
                        fontSize = 12.sp,
                    )
                }
                if (preguntaActual.urlVideo != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "[Video disponible]",
                        color = TextoSecundario,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        // Answer buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            preguntaActual.opciones.forEach { opcion ->
                BotonOpcion(
                    opcion = opcion,
                    opcionSeleccionada = datos.opcionSeleccionada,
                    mostrandoRetroalimentacion = datos.mostrandoRetroalimentacion,
                    onClick = { onSeleccionarOpcion(opcion.id) },
                )
            }
        }
    }
}

@Composable
private fun BotonOpcion(
    opcion: OpcionPregunta,
    opcionSeleccionada: String?,
    mostrandoRetroalimentacion: Boolean,
    onClick: () -> Unit,
) {
    val esSeleccionada = opcionSeleccionada == opcion.id
    val colorFondo: Color
    val colorBorde: Color
    val colorTexto: Color

    when {
        mostrandoRetroalimentacion && opcion.esCorrecta -> {
            colorFondo = VerdeClaro.copy(alpha = 0.2f)
            colorBorde = VerdeClaro
            colorTexto = VerdeClaro
        }
        mostrandoRetroalimentacion && esSeleccionada && !opcion.esCorrecta -> {
            colorFondo = Rojo.copy(alpha = 0.2f)
            colorBorde = Rojo
            colorTexto = Rojo
        }
        else -> {
            colorFondo = SuperficieElevada
            colorBorde = SuperficieElevada
            colorTexto = TextoPrimario
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .border(
                width = 1.5.dp,
                color = colorBorde,
                shape = RoundedCornerShape(8.dp),
            )
            .then(
                if (!mostrandoRetroalimentacion) {
                    Modifier.padding(0.dp)
                } else {
                    Modifier
                }
            ),
    ) {
        Button(
            onClick = onClick,
            enabled = !mostrandoRetroalimentacion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                contentColor = colorTexto,
                disabledContentColor = colorTexto,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = opcion.texto,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ─── PantallaResultado ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaResultado(navController: NavController, sesionId: String) {
    val koin = getKoin()
    val viewModel = remember { koin.get<ResultadoViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(sesionId) {
        viewModel.cargar(sesionId)
    }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is EventoResultado.IrAAprender -> navController.navigate(GrafoPrincipal) {
                    popUpTo(GrafoSesion) { inclusive = true }
                }
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
                    fontSize = 14.sp,
                    modifier = Modifier.padding(24.dp),
                )
            }
            is EstadoUi.Contenido -> {
                val datos = estado.datos
                TarjetaResultado(
                    xpGanado = datos.xpGanado,
                    respuestasCorrectas = datos.respuestasCorrectas,
                    totalPreguntas = datos.totalPreguntas,
                    onContinuar = { viewModel.continuar() },
                )
            }
        }
    }
}

@Composable
private fun TarjetaResultado(
    xpGanado: Int,
    respuestasCorrectas: Int,
    totalPreguntas: Int,
    onContinuar: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieOscura),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "¡Sesión Completada!",
                color = VerdeClaro,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            // XP earned
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AmbarSecundario,
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = "$xpGanado XP",
                        color = AmbarSecundario,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Text(
                    text = "puntos ganados",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                )
            }

            // Correct/total
            Box(
                modifier = Modifier
                    .background(SuperficieElevada, RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "$respuestasCorrectas / $totalPreguntas correctas",
                    color = TextoPrimario,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onContinuar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Continuar",
                    color = TextoPrimario,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─── PantallaTutorial ─────────────────────────────────────────────────────────

@Composable
fun PantallaTutorial(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Tutorial",
                color = VerdeClaro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Aprendé las normas viales antes de comenzar",
                color = TextoSecundario,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(com.vialgo.app.presentacion.navegacion.RutaAprender) },
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Comenzar", color = TextoPrimario, fontSize = 16.sp)
            }
        }
    }
}

// ─── PantallaClasificacion ───────────────────────────────────────────────────

@Composable
fun PantallaClasificacion(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Clasificación",
                color = VerdeClaro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Evaluación inicial para determinar tu nivel",
                color = TextoSecundario,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(
                        com.vialgo.app.presentacion.navegacion.RutaSesion(leccionId = "clasificacion-inicial")
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Comenzar", color = TextoPrimario, fontSize = 16.sp)
            }
        }
    }
}
