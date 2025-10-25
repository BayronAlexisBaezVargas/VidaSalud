package com.example.appvidasalud.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvidasalud.data.UserPreferencesRepository
import com.example.appvidasalud.model.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class HealthUiState(
    val userName: String = "María",
    val healthData: HealthData = HealthData()
)

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    private val _stepBaseline = MutableStateFlow(0)
    private val _stepBaselineDay = MutableStateFlow(0)

    // NUEVO: Estado para saber si los datos iniciales ya se cargaron
    private val _isDataLoaded = MutableStateFlow(false)


    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            userPrefs.healthDataFlow.firstOrNull()?.let { savedData ->

                // Comprobamos si el día guardado es el día actual
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                val isNewDay = savedData.stepBaselineDay != currentDay

                _stepBaseline.value = if (isNewDay) 0 else savedData.stepBaseline
                _stepBaselineDay.value = if (isNewDay) currentDay else savedData.stepBaselineDay

                _uiState.update { currentState ->
                    val updatedHealthData = currentState.healthData.copy(
                        calories = savedData.calories,
                        sleepHours = savedData.sleep,
                        // Si es un nuevo día, mostramos 0 pasos. Si no, mostramos los últimos guardados.
                        steps = if (isNewDay) 0 else savedData.savedSteps
                    )
                    currentState.copy(healthData = updatedHealthData)
                }

                // Marcamos que los datos ya se cargaron
                _isDataLoaded.value = true
            }
        }
    }

    fun processNewStepData(currentSensorValue: Int) {
        // No procesamos ningún dato del sensor hasta que los datos de DataStore se hayan cargado
        if (!_isDataLoaded.value) return

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        // Comprueba si el día guardado es diferente al día actual
        if (currentDay != _stepBaselineDay.value) {
            // ¡ES UN NUEVO DÍA!
            _stepBaseline.value = currentSensorValue
            _stepBaselineDay.value = currentDay

            viewModelScope.launch {
                userPrefs.saveStepBaseline(currentSensorValue, currentDay)
            }

            updateSteps(0)

        } else {
            // ES EL MISMO DÍA

            // Si la línea de base es 0 (porque es la primera vez que abrimos la app hoy)
            if (_stepBaseline.value == 0) {
                _stepBaseline.value = currentSensorValue
                viewModelScope.launch {
                    userPrefs.saveStepBaseline(currentSensorValue, currentDay)
                }
            }

            val stepsToday = currentSensorValue - _stepBaseline.value
            updateSteps(stepsToday)
        }
    }

    fun updateUserName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(userName = newName)
        }
    }

    fun updateSteps(stepCount: Int) {
        if (stepCount < 0) return

        _uiState.update { currentState ->
            val newHealthData = currentState.healthData.copy(steps = stepCount)
            currentState.copy(healthData = newHealthData)
        }

        viewModelScope.launch {
            userPrefs.saveSteps(stepCount)
        }
    }

    // ... (addCalories y updateSleep se quedan igual) ...
    fun addCalories(caloriesToAdd: Int) {
        if (caloriesToAdd <= 0) return
        var newTotalCalories = 0
        _uiState.update { currentState ->
            newTotalCalories = currentState.healthData.calories + caloriesToAdd
            val newHealthData = currentState.healthData.copy(calories = newTotalCalories)
            currentState.copy(healthData = newHealthData)
        }
        viewModelScope.launch {
            userPrefs.saveCalories(newTotalCalories)
        }
    }

    fun updateSleep(hours: Float) {
        if (hours < 0) return
        _uiState.update { currentState ->
            val newHealthData = currentState.healthData.copy(sleepHours = hours)
            currentState.copy(healthData = newHealthData)
        }
        viewModelScope.launch {
            userPrefs.saveSleep(hours)
        }
    }
}