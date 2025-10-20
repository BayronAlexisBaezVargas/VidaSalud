package com.example.appvidasalud.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appvidasalud.model.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Definimos un estado para la UI
data class HealthUiState(
    val userName: String = "María",
    val healthData: HealthData = HealthData()
)

class HealthViewModel : ViewModel() {

    // _uiState es privado y mutable, solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(HealthUiState())

    // uiState es público e inmutable, la UI lo observa para obtener datos.
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    // En una aplicación real, aquí llamarías a un repositorio para
    // obtener los datos desde una base de datos o una API.
    // Por ahora, usamos datos de ejemplo.
    fun updateUserName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(userName = newName)
        }
    }
}