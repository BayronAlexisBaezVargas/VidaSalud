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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.appvidasalud.model.HealthData
import com.example.appvidasalud.ui.theme.*
import com.example.appvidasalud.viewmodel.HealthViewModel
import com.example.appvidasalud.viewmodel.HealthViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import androidx.navigation.NavController

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

    // --- LÓGICA DE PERMISOS (SIN CAMBIOS) ---
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) { /* Permiso concedido */ } else { /* Permiso denegado */ }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) -> { /* Ya concedido */ }
                else -> { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) }
            }
        }
    }

    // --- LÓGICA DE SENSORES (SIMPLIFICADA) ---

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val stepSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    // Ya no necesitamos 'initialSteps' aquí
    // var initialSteps by remember { mutableStateOf(-1) } // <-- BORRAR ESTO

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {

                    val currentSensorValue = event.values[0].toInt()

                    // Simplemente enviamos el valor crudo al ViewModel.
                    // El ViewModel se encarga de la lógica.
                    healthViewModel.processNewStepData(currentSensorValue)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No es necesario
            }
        }
    }

    // Registra y cancela el listener (Sin cambios)
    DisposableEffect(Unit) {
        if (stepSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }




    var showCaloriesDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    if (showCaloriesDialog) {
        AddDataDialog(
            title = "Registrar Calorías",
            label = "Calorías consumidas",
            onDismiss = { showCaloriesDialog = false },
            onConfirm = { input ->
                val calories = input.toIntOrNull() ?: 0
                healthViewModel.addCalories(calories)
                showCaloriesDialog = false
            }
        )
    }

    if (showSleepDialog) {
        AddDataDialog(
            title = "Registrar Sueño",
            label = "Horas de sueño (ej: 7.5)",
            onDismiss = { showSleepDialog = false },
            onConfirm = { input ->
                val hours = input.toFloatOrNull() ?: 0f
                healthViewModel.updateSleep(hours)
                showSleepDialog = false
            }
        )
    }



    Scaffold(
        bottomBar = { AppBottomNavigation() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Header(
                userName = uiState.userName,
                onLogoutClicked = {

                    // YA NO NECESITAMOS BORRAR LOS PASOS AQUÍ
                    // initialSteps = -1  // <-- BORRAR ESTO
                    // healthViewModel.updateSteps(0) // <-- BORRAR ESTO

                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )

            // (El resto de la UI no cambia)
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
        }
    }
}


@Composable
fun Header(userName: String, onLogoutClicked: () -> Unit) {
// ... (código igual) ...
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
    ) {
        IconButton(
            onClick = onLogoutClicked,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Salir de la sesión",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VidaSalud",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tu compañero de salud física",
                color = TextWhite,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¡Hola, $userName!",
            )
            Text(
                text = "¿Listo para un día saludable?",
                color = TextWhite,
                fontSize = 16.sp
            )
        }
    }
}


// ... (SummaryGrid se queda igual) ...
@Composable
fun SummaryGrid(
    healthData: HealthData,
    onCaloriesCardClick: () -> Unit,
    onSleepCardClick: () -> Unit
) {
// ... (código igual) ...
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard(
                icon = Icons.Default.DirectionsWalk,
                value = NumberFormat.getNumberInstance(Locale.US).format(healthData.steps),
                label = "Pasos",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                value = healthData.calories.toString(),
                label = "Calorías",
                modifier = Modifier.weight(1f),
                onClick = onCaloriesCardClick
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard(
                icon = Icons.Default.WaterDrop,
                value = "${healthData.waterLiters}L",
                label = "Agua bebida",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                icon = Icons.Default.Bedtime,
                value = "${healthData.sleepHours}h",
                label = "Sueño",
                modifier = Modifier.weight(1f),
                onClick = onSleepCardClick
            )
        }
    }
}


// ... (StatCard se queda igual) ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
// ... (código igual) ...
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = IconTintColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}


// ... (QuickActionsSection se queda igual) ...
@Composable
fun QuickActionsSection(
    navController: NavController,
    onCaloriesClick: () -> Unit
) {
// ... (código igual) ...
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Acciones Rápidas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Entrenar", Icons.Default.FitnessCenter, OrangeAction, Modifier.weight(1f)) {
                navController.navigate("training")
            }
            QuickActionButton("Beber Agua", Icons.Default.LocalDrink, BlueAction, Modifier.weight(1f)) { /* Acción futura */ }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            QuickActionButton(
                text = "Registrar Comida",
                icon = Icons.Default.Restaurant,
                color = PinkAction,
                modifier = Modifier.weight(1f),
                onClick = onCaloriesClick
            )

            QuickActionButton("Peso", Icons.Default.MonitorWeight, YellowAction, Modifier.weight(1f)) { /* Acción futura */ }
        }
    }
}


// ... (QuickActionButton se queda igual) ...
@Composable
fun QuickActionButton(text: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
// ... (código igual) ...
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


// ... (DailyProgressSection se queda igual) ...
@Composable
fun DailyProgressSection(progress: Float) {
// ... (código igual) ...
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GreenPrimary,
                    trackColor = Color.LightGray
                )
            }
        }
    }
}


// ... (AppBottomNavigation se queda igual) ...
@Composable
fun AppBottomNavigation() {
// ... (código igual) ...
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Inicio", "Estadísticas", "Metas", "Perfil")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.BarChart, Icons.Filled.CheckCircle, Icons.Filled.Person)

    NavigationBar(
        containerColor = CardBackground,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}


// ... (AddDataDialog se queda igual) ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
// ... (código igual) ...
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textInput) }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}