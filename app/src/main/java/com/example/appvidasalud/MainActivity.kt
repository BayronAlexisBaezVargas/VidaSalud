package com.example.appvidasalud
// Tu paquete principal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appvidasalud.ui.theme.navigation.AppNavigation // <-- Importa la navegación
import com.example.appvidasalud.ui.theme.AppVidaSaludTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppVidaSaludTheme {
                // Llamamos a AppNavigation, que será el punto de entrada de la UI
                AppNavigation()
            }
        }
    }
}