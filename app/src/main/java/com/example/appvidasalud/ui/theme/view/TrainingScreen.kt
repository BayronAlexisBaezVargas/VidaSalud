package com.example.appvidasalud.ui.theme.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvidasalud.ui.theme.CardBackground
import com.example.appvidasalud.ui.theme.GreenPrimary
import com.example.appvidasalud.ui.theme.ScreenBackground
import com.example.appvidasalud.ui.theme.TextWhite
import com.example.appvidasalud.viewmodel.TrainingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    navController: NavController,
    viewModel: TrainingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val recordedSessions by viewModel.recordedSessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Entrenamiento", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        },
        containerColor = ScreenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Campo para el nombre del ejercicio
            OutlinedTextField(
                value = state.exerciseName,
                onValueChange = viewModel::updateExerciseName,
                label = { Text("Nombre del Ejercicio") },
                placeholder = { Text("Ej: HIIT, Correr, Pesas") },
                leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Pantalla del Cronómetro (Iniciar, Detener, Vuelta)
            Text(
                text = viewModel.formatTime(state.timeMillis),
                fontSize = 60.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 3. Botones de Control del Cronómetro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón Reiniciar
                Button(
                    onClick = viewModel::reset,
                    enabled = !state.isRunning && state.timeMillis > 0L,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reiniciar")
                }

                // Botón Iniciar/Pausa
                Button(
                    onClick = viewModel::toggleStartPause,
                    // Solo se puede iniciar si hay nombre de ejercicio o ya está corriendo/pausado.
                    enabled = state.timeMillis > 0L || state.exerciseName.isNotBlank() || !state.isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    if (state.isRunning) {
                        Icon(Icons.Default.Pause, contentDescription = "Pausa")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pausa")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.timeMillis == 0L) "Comenzar" else "Continuar")
                    }
                }

                // Botón Vuelta
                Button(
                    onClick = viewModel::lap,
                    enabled = state.isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Vuelta")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vuelta")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // 4. Botón para Registrar Entrenamiento (Visible cuando está Pausado y hay tiempo)
            if (!state.isRunning && state.timeMillis > 0L) {
                Button(
                    onClick = viewModel::recordTrainingSession,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Registrar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Entrenamiento")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 5. Lista de Vueltas (utiliza LazyColumn para un mejor rendimiento)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Registro de Vueltas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.laps.isEmpty()) {
                        Text("No hay vueltas registradas.", color = MaterialTheme.colorScheme.outline)
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(state.laps.reversed()) { lap ->
                                Text(lap, modifier = Modifier.padding(vertical = 4.dp))
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}
