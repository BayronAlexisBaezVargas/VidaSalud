package com.example.appvidasalud.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar // Necesario para el Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class HealthDataValues(
    val calories: Int,
    val sleep: Float,
    val stepBaseline: Int,
    val stepBaselineDay: Int,
    val savedSteps: Int
)

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
        val CALORIES_KEY = intPreferencesKey("calories")
        val SLEEP_HOURS_KEY = floatPreferencesKey("sleep_hours")

        val STEP_BASELINE_KEY = intPreferencesKey("step_baseline")
        val STEP_BASELINE_DAY_KEY = intPreferencesKey("step_baseline_day")
        val SAVED_STEPS_KEY = intPreferencesKey("saved_steps")
    }

    // ... (saveUserCredentials, userNameFlow, userCredentialsFlow se quedan igual) ...
    suspend fun saveUserCredentials(userName: String, pass: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[USER_PASSWORD_KEY] = pass
        }
    }
    val userNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }
    val userCredentialsFlow: Flow<Pair<String?, String?>> = context.dataStore.data.map { preferences ->
        val userName = preferences[USER_NAME_KEY]
        val password = preferences[USER_PASSWORD_KEY]
        Pair(userName, password)
    }


    val healthDataFlow: Flow<HealthDataValues> = context.dataStore.data
        .map { preferences ->
            val calories = preferences[CALORIES_KEY] ?: 0
            val sleep = preferences[SLEEP_HOURS_KEY] ?: 0f

            // --- ¡AQUÍ ESTABA EL BUG! ---
            // El día por defecto debe ser '0' para forzar la lógica del "nuevo día"
            // si la app se instala por primera vez.
            val baseline = preferences[STEP_BASELINE_KEY] ?: 0
            val baselineDay = preferences[STEP_BASELINE_DAY_KEY] ?: 0 // <-- CAMBIADO DE currentDay a 0
            val savedSteps = preferences[SAVED_STEPS_KEY] ?: 0

            HealthDataValues(calories, sleep, baseline, baselineDay, savedSteps)
        }

    // ... (saveCalories y saveSleep se quedan igual) ...
    suspend fun saveCalories(calories: Int) {
        context.dataStore.edit { preferences ->
            preferences[CALORIES_KEY] = calories
        }
    }
    suspend fun saveSleep(sleep: Float) {
        context.dataStore.edit { preferences ->
            preferences[SLEEP_HOURS_KEY] = sleep
        }
    }


    // --- Funciones para guardar pasos (se quedan igual) ---
    suspend fun saveStepBaseline(baseline: Int, day: Int) {
        context.dataStore.edit { preferences ->
            preferences[STEP_BASELINE_KEY] = baseline
            preferences[STEP_BASELINE_DAY_KEY] = day
        }
    }

    suspend fun saveSteps(steps: Int) {
        context.dataStore.edit { preferences ->
            preferences[SAVED_STEPS_KEY] = steps
        }
    }
}