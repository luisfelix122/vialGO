package com.vialgo.app.presentacion.aprender

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vialgo.app.dominio.comun.EstadoUi
import com.vialgo.app.dominio.entidades.Leccion
import com.vialgo.app.dominio.entidades.Modulo
import com.vialgo.app.dominio.entidades.ProgresoLeccion
import com.vialgo.app.dominio.entidades.RolUsuario
import com.vialgo.app.presentacion.navegacion.RutaSesion
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAprender(navController: NavController) {
    val koin = getKoin()
    val viewModel = remember { koin.get<AprenderViewModel>() }

    val estadoUi by viewModel.estadoUi.collectAsState()
    val modulosExpandidos = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        viewModel.cargarContenido(usuarioId = "test-user", rol = RolUsuario.CONDUCTOR)
    }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is EventoAprender.IrASesion -> navController.navigate(RutaSesion(evento.leccionId))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "VialGo",
                        color = VerdeClaro,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    when (val estado = estadoUi) {
                        is EstadoUi.Contenido -> {
                            IndicadorVidas(vidas = estado.datos.vidasRestantes)
                        }
                        else -> Unit
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
                    if (datos.modulos.isEmpty()) {
                        Text(
                            text = "No hay contenido disponible",
                            color = TextoSecundario,
                            fontSize = 16.sp,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            items(datos.modulos, key = { it.id }) { modulo ->
                                val expandido = modulosExpandidos[modulo.id] ?: true
                                TarjetaModulo(
                                    modulo = modulo,
                                    progresos = datos.progresos,
                                    expandido = expandido,
                                    onExpandirToggle = {
                                        modulosExpandidos[modulo.id] = !expandido
                                    },
                                    onLeccionSeleccionada = { leccionId ->
                                        viewModel.navegarASesion(leccionId)
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorVidas(vidas: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Vidas",
            tint = Rojo,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$vidas",
            color = TextoPrimario,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TarjetaModulo(
    modulo: Modulo,
    progresos: Map<String, ProgresoLeccion>,
    expandido: Boolean,
    onExpandirToggle: () -> Unit,
    onLeccionSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SuperficieOscura),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandirToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = modulo.nombre,
                        color = TextoPrimario,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (modulo.descripcion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = modulo.descripcion,
                            color = TextoSecundario,
                            fontSize = 12.sp,
                        )
                    }
                }
                Icon(
                    imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expandido) "Colapsar" else "Expandir",
                    tint = TextoSecundario,
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                ) {
                    modulo.lecciones.forEach { leccion ->
                        val progreso = progresos[leccion.id]
                        ItemLeccion(
                            leccion = leccion,
                            progreso = progreso,
                            onClick = { onLeccionSeleccionada(leccion.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemLeccion(
    leccion: Leccion,
    progreso: ProgresoLeccion?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(SuperficieElevada)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = leccion.nombre,
            color = TextoPrimario,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Row {
            val estrellas = progreso?.estrellas ?: 0
            repeat(3) { indice ->
                Icon(
                    imageVector = if (indice < estrellas) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (indice < estrellas) AmbarSecundario else TextoSecundario,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
