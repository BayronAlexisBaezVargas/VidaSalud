package com.example.appvidasalud.ui.theme.view

// NUEVO: Importaciones necesarias para el sensor y permisos
import android.Manifest
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext // NUEVO: Necesario para el contexto
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat // NUEVO: Necesario para chequear permisos
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appvidasalud.model.HealthData
import com.example.appvidasalud.ui.theme.*
import com.example.appvidasalud.viewmodel.HealthViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    healthViewModel: HealthViewModel = viewModel(),
    userName: String
) {
    LaunchedEffect(key1 = userName) {
        healthViewModel.updateUserName(userName)
    }

    val uiState by healthViewModel.uiState.collectAsState()
    val healthData = uiState.healthData

    // --- LÓGICA DE SENSORES Y PERMISOS ---

    // NUEVO: Obtenemos el contexto
    val context = LocalContext.current

    // NUEVO: Launcher para pedir el permiso de ACTIVITY_RECOGNITION
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // El permiso fue concedido.
        } else {
            // El permiso fue denegado. Podrías mostrar un Snackbar.
        }
    }

    // NUEVO: Pedir permiso al cargar la pantalla (si es necesario)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Q = API 29
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) -> {
                    // El permiso ya está concedido
                }
                else -> {
                    // Pedir el permiso
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
    }

    // NUEVO: Configuración del Sensor Manager y el Listener
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val stepSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    // NUEVO: El listener que reaccionará a los eventos del sensor
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values[0].toInt()
                    // Actualizamos el ViewModel con el nuevo conteo de pasos
                    healthViewModel.updateSteps(steps)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No es necesario para este caso
            }
        }
    }

    // NUEVO: Registra y cancela el listener según el ciclo de vida de la pantalla
    DisposableEffect(Unit) {
        // Registra el listener cuando el composable entra en pantalla
        if (stepSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Se ejecuta cuando el composable sale de la pantalla
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    // --- FIN DE LÓGICA DE SENSORES ---


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
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
            // El SummaryGrid ahora mostrará los pasos leídos del sensor
            SummaryGrid(healthData = healthData)
            QuickActionsSection(navController = navController)
            DailyProgressSection(progress = healthData.stepGoalProgress)
        }
    }
}

// ... (El resto del código de HomeScreen.kt no necesita cambios)
// Header, SummaryGrid, StatCard, QuickActionsSection, QuickActionButton,
// DailyProgressSection, y AppBottomNavigation se quedan igual.

@Composable
fun Header(userName: String, onLogoutClicked: () -> Unit) { // <-- Nuevo parámetro
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

@Composable
fun SummaryGrid(healthData: HealthData) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard(
                icon = Icons.Default.DirectionsWalk,
                value = NumberFormat.getNumberInstance(Locale.US).format(healthData.steps),
                label = "Pasos hoy",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                value = healthData.calories.toString(),
                label = "Calorías",
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
@Composable
fun QuickActionsSection(navController: NavController) {
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
            QuickActionButton("Registrar Comida", Icons.Default.Restaurant, PinkAction, Modifier.weight(1f)) { /* Acción futura */ }
            QuickActionButton("Peso", Icons.Default.MonitorWeight, YellowAction, Modifier.weight(1f)) { /* Acción futura */ }
        }
    }
}
@Composable
fun QuickActionButton(text: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, // Usamos el lambda de onClick
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

@Composable
fun AppBottomNavigation() {
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