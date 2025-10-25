package com.example.appvidasalud.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
    }
    suspend fun saveUserCredentials(userName: String, pass: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[USER_PASSWORD_KEY] = pass
        }
    }
    val userNameFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY]
        }
    val userCredentialsFlow: Flow<Pair<String?, String?>> = context.dataStore.data
        .map { preferences ->
            val userName = preferences[USER_NAME_KEY]
            val password = preferences[USER_PASSWORD_KEY]
            Pair(userName, password)
        }
}
