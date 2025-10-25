package com.example.appvidasalud.model

data class HealthData(
    val steps: Int = 0,
    val calories: Int = 0,      // <-- CAMBIA ESTO (de 342 a 0)
    val waterLiters: Float = 1.2f,
    val sleepHours: Float = 0f, // <-- CAMBIA ESTO (de 7.5f a 0f)
    val stepGoalProgress: Float = 0.75f
)