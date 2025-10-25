package com.example.appvidasalud.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
data class StopwatchState(
    val timeMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<String> = emptyList(),
    val exerciseName: String = ""
)
data class RecordedSession(
    val exerciseName: String,
    val durationSeconds: Long,
    val estimatedCalories: Int
)

class TrainingViewModel : ViewModel() {
    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()
    private val _recordedSessions = MutableStateFlow<List<RecordedSession>>(emptyList())
    val recordedSessions: StateFlow<List<RecordedSession>> = _recordedSessions.asStateFlow()

    private var timerJob: Job? = null
    private fun startTimer() {
        if (timerJob?.isActive == true) return
        _state.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100L)
                _state.update { it.copy(timeMillis = it.timeMillis + 100L) }
            }
        }
    }
    fun toggleStartPause() {
        if (_state.value.isRunning) {
            timerJob?.cancel()
            _state.update { it.copy(isRunning = false) }
        } else {
            startTimer()
        }
    }
    fun lap() {
        val currentTime = formatTime(_state.value.timeMillis)
        val newLap = "Vuelta #${_state.value.laps.size + 1}: $currentTime"
        _state.update { it.copy(laps = it.laps + newLap) }
    }
    fun reset() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                timeMillis = 0L,
                isRunning = false,
                laps = emptyList()
            )
        }
    }

    fun updateExerciseName(name: String) {
        _state.update { it.copy(exerciseName = name) }
    }
    fun recordTrainingSession() {
        if (_state.value.timeMillis == 0L) return

        val durationSeconds = _state.value.timeMillis / 1000L
        val durationMinutes = durationSeconds / 60.0
        val estimatedCalories = (durationMinutes * 5).toInt()

        val newSession = RecordedSession(
            exerciseName = _state.value.exerciseName.ifBlank { "Entrenamiento Rápido" },
            durationSeconds = durationSeconds,
            estimatedCalories = estimatedCalories
        )

        _recordedSessions.update { it + newSession }
        reset()
        _state.update { it.copy(exerciseName = "") }
    }
    fun formatTime(timeMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60
        val milliseconds = (timeMillis / 100) % 10

        return String.format("%02d:%02d.%d", minutes, seconds, milliseconds)
    }
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
