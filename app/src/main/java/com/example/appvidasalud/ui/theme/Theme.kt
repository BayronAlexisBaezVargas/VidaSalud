package com.example.appvidasalud.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- PALETA DE COLORES PERSONALIZADA ---

// Modo Claro (Tus colores existentes)
val GreenPrimary = Color(0xFF4CAF50)
val TextWhite = Color.White
val CardBackground = Color.White
val ScreenBackground = Color(0xFFF0F4F7)
val IconTintColor = Color(0xFF757575)
val LightTextColor = Color.Black

// Modo Oscuro (Nuevos colores)
val GreenPrimaryDark = Color(0xFFA5D6A7) // Un verde más claro para el modo oscuro
val DarkScreenBackground = Color(0xFF121212) // Fondo oscuro estándar
val DarkCardBackground = Color(0xFF1E1E1E)   // Fondo de tarjeta oscuro (ligeramente más claro)
val DarkIconTint = Color(0xFFBDBDBD)       // Tinte de ícono claro
val DarkTextColor = Color.White             // Texto principal en modo oscuro

// Colores para los botones de acciones rápidas (se mantienen)
val OrangeAction = Color(0xFFFE7575)
val BlueAction = Color(0xFF5A9DFF)
val PinkAction = Color(0xFFE86B9E)
val YellowAction = Color(0xFFF7C168)


// --- DEFINICIÓN DE ESQUEMAS DE COLOR ---

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,         // Verde claro
    onPrimary = Color.Black,            // Texto sobre el verde claro
    background = DarkScreenBackground,    // Fondo de pantalla oscuro
    onBackground = DarkTextColor,         // Texto sobre el fondo oscuro (blanco)
    surface = DarkCardBackground,       // Fondo de tarjetas oscuro
    onSurface = DarkTextColor,          // Texto sobre las tarjetas (blanco)
    onSurfaceVariant = DarkIconTint,      // Color para íconos/texto secundario
    secondary = BlueAction,
    tertiary = PinkAction
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,             // Verde principal
    onPrimary = TextWhite,              // Texto sobre el verde (blanco)
    background = ScreenBackground,        // Fondo de pantalla claro
    onBackground = LightTextColor,        // Texto sobre el fondo claro (negro)
    surface = CardBackground,           // Fondo de tarjetas claro (blanco)
    onSurface = LightTextColor,         // Texto sobre las tarjetas (negro)
    onSurfaceVariant = IconTintColor,       // Color para íconos/texto secundario
    secondary = BlueAction,
    tertiary = PinkAction
)

@Composable
fun AppVidaSaludTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // ¡¡¡AQUÍ ESTÁ EL CAMBIO!!!
    // Pasa de 'true' a 'false' para forzar tu tema verde
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}