package com.example.appvidasalud.ui.theme.navigation

// Imports para las animaciones estándar
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
// Imports estándar
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// --- ¡¡IMPORTACIONES CLAVE!! ---
// ¡¡Usamos las importaciones normales!!
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// --- FIN DE IMPORTACIONES CLAVE ---

// Tus pantallas
import com.example.appvidasalud.ui.theme.view.FoodLogScreen
import com.example.appvidasalud.ui.theme.view.HomeScreen
import com.example.appvidasalud.ui.theme.view.LoginScreen
import com.example.appvidasalud.ui.theme.view.RegisterFoodScreen
import com.example.appvidasalud.ui.theme.view.RegisterScreen
import com.example.appvidasalud.ui.theme.view.TrainingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Define una animación estándar de deslizamiento
    val slideDuration = 400
    val slideIn = AnimatedContentTransitionScope.SlideDirection.Left
    val slideOut = AnimatedContentTransitionScope.SlideDirection.Right

    // --- ¡USAMOS NavHost! ---
    NavHost(
        navController = navController,
        startDestination = "login",

        // --- ¡AQUÍ SE AÑADEN LAS ANIMACIONES POR DEFECTO! ---
        enterTransition = { fadeIn(animationSpec = tween(slideDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(slideDuration)) },
        popEnterTransition = { fadeIn(animationSpec = tween(slideDuration)) },
        popExitTransition = { fadeOut(animationSpec = tween(slideDuration)) }
    ) {

        // --- ¡USAMOS composable! ---
        composable(
            route = "login"
            // No necesita animaciones extra, usa las del NavHost
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            route = "register",
            // Podemos sobreescribir las animaciones para que esta deslice
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            RegisterScreen(navController = navController)
        }

        composable(
            route = "home/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
            // No necesita animaciones extra, usa las del NavHost
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            HomeScreen(navController = navController, userName = userName)
        }

        composable(
            route = "training",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            TrainingScreen(navController = navController)
        }

        composable(
            route = "food_log",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            FoodLogScreen(navController = navController)
        }

        composable(
            route = "register_food",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            RegisterFoodScreen(navController = navController)
        }
    }
}