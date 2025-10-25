package com.example.appvidasalud.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appvidasalud.model.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
data class HealthUiState(
    val userName: String = "María",
    val healthData: HealthData = HealthData()
)

class HealthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()
    fun updateUserName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(userName = newName)
        }
    }
}