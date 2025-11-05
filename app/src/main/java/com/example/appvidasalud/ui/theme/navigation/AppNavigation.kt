package com.example.appvidasalud.ui.theme.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appvidasalud.ui.theme.view.FoodLogScreen
import com.example.appvidasalud.ui.theme.view.LoginScreen
import com.example.appvidasalud.ui.theme.view.MainScreen
import com.example.appvidasalud.ui.theme.view.RegisterFoodScreen
import com.example.appvidasalud.ui.theme.view.RegisterScreen
import com.example.appvidasalud.ui.theme.view.TrainingScreen

@Composable
fun AppNavigation() {
    // Este navController maneja la navegación "global" (Login <-> MainScreen <-> Pantallas completas)
    val mainNavController = rememberNavController()

    val slideDuration = 400
    val slideIn = AnimatedContentTransitionScope.SlideDirection.Left
    val slideOut = AnimatedContentTransitionScope.SlideDirection.Right

    NavHost(
        navController = mainNavController,
        startDestination = "login",
        enterTransition = { fadeIn(animationSpec = tween(slideDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(slideDuration)) },
        popEnterTransition = { fadeIn(animationSpec = tween(slideDuration)) },
        popExitTransition = { fadeOut(animationSpec = tween(slideDuration)) }
    ) {

        // --- Autenticación ---
        composable("login") {
            LoginScreen(navController = mainNavController)
        }
        composable(
            route = "register",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            RegisterScreen(navController = mainNavController)
        }

        // --- Pantalla Principal (Con Barra Inferior) ---
        composable(
            route = "home/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            // Aquí cargamos MainScreen, que contiene el Scaffold con la BottomBar
            MainScreen(userName = userName, mainNavController = mainNavController)
        }

        // --- Pantallas Completas (Sin barra inferior) ---
        // Estas pantallas tapan toda la UI, incluida la barra inferior, lo cual es ideal para flujos específicos.
        composable(
            route = "training",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            TrainingScreen(navController = mainNavController)
        }

        composable(
            route = "food_log",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            FoodLogScreen(navController = mainNavController)
        }

        composable(
            route = "register_food",
            enterTransition = { slideIntoContainer(slideIn, animationSpec = tween(slideDuration)) },
            exitTransition = { slideOutOfContainer(slideOut, animationSpec = tween(slideDuration)) }
        ) {
            RegisterFoodScreen(navController = mainNavController)
        }
    }
}