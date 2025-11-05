package com.example.appvidasalud.ui.theme.view

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvidasalud.model.HealthData
import com.example.appvidasalud.ui.theme.*
import com.example.appvidasalud.viewmodel.HealthViewModel
import com.example.appvidasalud.viewmodel.HealthViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    healthViewModel: HealthViewModel = viewModel(
        factory = HealthViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    userName: String
) {
    LaunchedEffect(key1 = userName) {
        healthViewModel.updateUserName(userName)
    }

    val uiState by healthViewModel.uiState.collectAsState()
    val healthData = uiState.healthData
    val context = LocalContext.current

    // --- Lógica de Permisos ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* No-op */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    // --- Lógica de Sensores ---
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val stepSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    healthViewModel.processNewStepData(event.values[0].toInt())
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        if (stepSensor != null) {
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    // --- Diálogos ---
    var showCaloriesDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    if (showCaloriesDialog) {
        AddDataDialog(
            title = "Registrar Calorías",
            label = "Calorías consumidas",
            onDismiss = { showCaloriesDialog = false },
            onConfirm = { healthViewModel.addCalories(it.toIntOrNull() ?: 0); showCaloriesDialog = false }
        )
    }
    if (showSleepDialog) {
        AddDataDialog(
            title = "Registrar Sueño",
            label = "Horas de sueño (ej: 7.5)",
            onDismiss = { showSleepDialog = false },
            onConfirm = { healthViewModel.updateSleep(it.toFloatOrNull() ?: 0f); showSleepDialog = false }
        )
    }

    // --- Contenido Principal (Sin Scaffold) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Header(
            userName = uiState.userName,
            onLogoutClicked = {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        )

        SummaryGrid(
            healthData = healthData,
            onCaloriesCardClick = { showCaloriesDialog = true },
            onSleepCardClick = { showSleepDialog = true }
        )

        QuickActionsSection(
            navController = navController,
            onCaloriesClick = { navController.navigate("food_log") }
        )

        DailyProgressSection(progress = healthData.stepGoalProgress)

        // Espacio extra al final para que no quede pegado al borde inferior si hay scroll
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun Header(userName: String, onLogoutClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
    ) {
        IconButton(
            onClick = onLogoutClicked,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Salir", tint = Color.White)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("VidaSalud", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Tu compañero de salud física", color = TextWhite, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text("¡Hola, $userName!", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¿Listo para un día saludable?", color = TextWhite, fontSize = 16.sp)
        }
    }
}

@Composable
fun SummaryGrid(healthData: HealthData, onCaloriesCardClick: () -> Unit, onSleepCardClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(Icons.Default.DirectionsWalk, NumberFormat.getNumberInstance(Locale.US).format(healthData.steps), "Pasos", Modifier.weight(1f))
            StatCard(Icons.Default.LocalFireDepartment, healthData.calories.toString(), "Calorías", Modifier.weight(1f), onClick = onCaloriesCardClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(Icons.Default.WaterDrop, "${healthData.waterLiters}L", "Agua bebida", Modifier.weight(1f))
            StatCard(Icons.Default.Bedtime, "${healthData.sleepHours}h", "Sueño", Modifier.weight(1f), onClick = onSleepCardClick)
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .aspectRatio(1f)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = IconTintColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController, onCaloriesClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Acciones Rápidas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Entrenar", Icons.Default.FitnessCenter, OrangeAction, Modifier.weight(1f)) { navController.navigate("training") }
            QuickActionButton("Beber Agua", Icons.Default.LocalDrink, BlueAction, Modifier.weight(1f)) { /* TODO */ }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Registrar Comida", Icons.Default.Restaurant, PinkAction, Modifier.weight(1f), onClick = onCaloriesClick)
            QuickActionButton("Peso", Icons.Default.MonitorWeight, YellowAction, Modifier.weight(1f)) { /* TODO */ }
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier.height(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DailyProgressSection(progress: Float) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Progreso Diario", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Meta de Pasos", style = MaterialTheme.typography.titleMedium)
                    Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = GreenPrimary,
                    trackColor = Color.LightGray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataDialog(title: String, label: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var textInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = { Button(onClick = { onConfirm(textInput) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}