package com.example.appvidasalud.model

data class HealthData(
    val steps: Int = 0, // <-- CAMBIA ESTO (de 8547 a 0)
    val calories: Int = 342,
    val waterLiters: Float = 1.2f,
    val sleepHours: Float = 7.5f,
    val stepGoalProgress: Float = 0.75f // Esto después lo puedes calcular dinámicamente
)