package com.example.appvidasalud.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appvidasalud.ui.theme.view.HomeScreen
import com.example.appvidasalud.ui.theme.view.LoginScreen
import com.example.appvidasalud.ui.theme.view.RegisterScreen // <-- Importar pantalla
import com.example.appvidasalud.ui.theme.view.TrainingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController = navController)
        }

        // --- AÑADIR ESTA RUTA NUEVA ---
        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable(
            route = "home/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            HomeScreen(navController = navController, userName = userName)
        }
        composable("training") {
            TrainingScreen(navController = navController)
        }
    }
}