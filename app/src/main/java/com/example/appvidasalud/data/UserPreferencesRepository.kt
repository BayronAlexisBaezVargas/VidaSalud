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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class HealthDataValues(
    val calories: Int,
    val sleep: Float,
    val stepBaseline: Int,
    val stepBaselineDay: Int,
    val savedSteps: Int
)

// Clase de datos para cargar todo el perfil de una vez
data class UserProfile(
    val name: String,
    val email: String,
    val phone: String
)

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
        // NUEVAS CLAVES
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_PHONE_KEY = stringPreferencesKey("user_phone")

        val CALORIES_KEY = intPreferencesKey("calories")
        val SLEEP_HOURS_KEY = floatPreferencesKey("sleep_hours")
        val STEP_BASELINE_KEY = intPreferencesKey("step_baseline")
        val STEP_BASELINE_DAY_KEY = intPreferencesKey("step_baseline_day")
        val SAVED_STEPS_KEY = intPreferencesKey("saved_steps")
    }

    // --- Autenticación y Perfil ---
    suspend fun saveUserCredentials(userName: String, pass: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[USER_PASSWORD_KEY] = pass
            // Valores por defecto al crear cuenta
            if (preferences[USER_EMAIL_KEY] == null) preferences[USER_EMAIL_KEY] = "usuario@ejemplo.com"
        }
    }

    // NUEVO: Función para actualizar perfil completo
    suspend fun updateUserProfile(name: String, email: String, phone: String, newPassword: String?) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_PHONE_KEY] = phone
            // Solo actualizamos la contraseña si no está vacía
            if (!newPassword.isNullOrBlank()) {
                preferences[USER_PASSWORD_KEY] = newPassword
            }
        }
    }

    // Flujo para obtener los datos del perfil
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[USER_NAME_KEY] ?: "Usuario",
            email = preferences[USER_EMAIL_KEY] ?: "",
            phone = preferences[USER_PHONE_KEY] ?: ""
        )
    }

    val userCredentialsFlow: Flow<Pair<String?, String?>> = context.dataStore.data.map { preferences ->
        Pair(preferences[USER_NAME_KEY], preferences[USER_PASSWORD_KEY])
    }

    // --- Datos de Salud (Sin cambios) ---
    val healthDataFlow: Flow<HealthDataValues> = context.dataStore.data.map { preferences ->
        HealthDataValues(
            preferences[CALORIES_KEY] ?: 0,
            preferences[SLEEP_HOURS_KEY] ?: 0f,
            preferences[STEP_BASELINE_KEY] ?: 0,
            preferences[STEP_BASELINE_DAY_KEY] ?: 0,
            preferences[SAVED_STEPS_KEY] ?: 0
        )
    }

    suspend fun saveCalories(calories: Int) {
        context.dataStore.edit { it[CALORIES_KEY] = calories }
    }
    suspend fun saveSleep(sleep: Float) {
        context.dataStore.edit { it[SLEEP_HOURS_KEY] = sleep }
    }
    suspend fun saveStepBaseline(baseline: Int, day: Int) {
        context.dataStore.edit {
            it[STEP_BASELINE_KEY] = baseline
            it[STEP_BASELINE_DAY_KEY] = day
        }
    }
    suspend fun saveSteps(steps: Int) {
        context.dataStore.edit { it[SAVED_STEPS_KEY] = steps }
    }
}