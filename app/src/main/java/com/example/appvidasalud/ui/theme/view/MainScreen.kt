package com.example.appvidasalud.ui.theme.view

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
import com.example.appvidasalud.ui.theme.GreenPrimary

@Composable
fun MainScreen(userName: String, mainNavController: androidx.navigation.NavController) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
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
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = GreenPrimary.copy(alpha = 0.2f)
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
                // Pasamos 'mainNavController' para navegaciones externas (Training, FoodLog, Login)
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
                        // Navega al login y limpia TODA la pila de navegación para que no se pueda volver atrás
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