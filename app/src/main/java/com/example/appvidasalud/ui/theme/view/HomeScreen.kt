package com.example.appvidasalud.ui.theme.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appvidasalud.model.HealthData
import com.example.appvidasalud.ui.theme.*
import com.example.appvidasalud.viewmodel.HealthViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.navigation.NavController
// Y asegúrate de tener también el import para el NavHost
import androidx.navigation.compose.rememberNavController
@Composable
fun HomeScreen(
    // Añadimos NavController como parámetro
    navController: NavController,
    healthViewModel: HealthViewModel = viewModel(),
    userName: String
) {
    LaunchedEffect(key1 = userName) {
        healthViewModel.updateUserName(userName)
    }

    val uiState by healthViewModel.uiState.collectAsState()
    val healthData = uiState.healthData

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
            // Pasamos la acción de logout al Header
            Header(
                userName = uiState.userName,
                onLogoutClicked = {
                    // Lógica para salir de la sesión
                    navController.navigate("login") {
                        // Limpia todas las pantallas anteriores del historial de navegación
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
            SummaryGrid(healthData = healthData)
            QuickActionsSection()
            DailyProgressSection(progress = healthData.stepGoalProgress)
        }
    }
}

@Composable
fun Header(userName: String, onLogoutClicked: () -> Unit) { // <-- Nuevo parámetro
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
    ) {
        // Botón para salir en la esquina superior derecha
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
                // ... (el resto del Header no cambia)
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
fun QuickActionsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Acciones Rápidas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Entrenar", Icons.Default.FitnessCenter, OrangeAction, Modifier.weight(1f))
            QuickActionButton("Beber Agua", Icons.Default.LocalDrink, BlueAction, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Registrar Comida", Icons.Default.Restaurant, PinkAction, Modifier.weight(1f))
            QuickActionButton("Peso", Icons.Default.MonitorWeight, YellowAction, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* Acción a realizar */ },
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