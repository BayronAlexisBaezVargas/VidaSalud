package com.example.appvidasalud.ui.theme.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
// No importes GreenPrimary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(userName: String, mainNavController: androidx.navigation.NavController) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                // containerColor = Color.White, // <-- Se elimina esta línea
                // tonalElevation = 8.dp // Opcional, puedes dejarla o quitarla
            ) {
                val items = listOf(
                    NavigationItem("home", "Inicio", Icons.Default.Home),
                    NavigationItem("stats", "Estadísticas", Icons.Default.BarChart),
                    NavigationItem("goals", "Metas", Icons.Default.CheckCircle),
                    NavigationItem("profile", "Perfil", Icons.Default.Person)
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            homeNavController.navigate(item.route) {
                                popUpTo(homeNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            // Los colores del tema se encargarán del resto
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = mainNavController, userName = userName)
            }
            composable("stats") {
                // Placeholder para Estadísticas
                Text("Pantalla de Estadísticas", modifier = Modifier.padding(16.dp))
            }
            composable("goals") {
                GoalsScreen()
            }
            composable("profile") {
                ProfileScreen(
                    navController = mainNavController,
                    onLogout = {
                        mainNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

data class NavigationItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)