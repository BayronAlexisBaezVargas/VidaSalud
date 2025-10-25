package com.example.appvidasalud.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appvidasalud.model.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HealthUiState(
    val userName: String = "María",
    val healthData: HealthData = HealthData() // <-- Ya se crea con steps = 0
)

class HealthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()
    fun updateUserName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(userName = newName)
        }
    }

    // --- AÑADE ESTA FUNCIÓN ---
    // Esta función será llamada por el listener del sensor
    fun updateSteps(stepCount: Int) {
        _uiState.update { currentState ->
            // Creamos una nueva copia de healthData con los pasos actualizados
            val newHealthData = currentState.healthData.copy(steps = stepCount)
            // Actualizamos el estado
            currentState.copy(healthData = newHealthData)
        }
    }
    // --- FIN DE LA NUEVA FUNCIÓN ---
}