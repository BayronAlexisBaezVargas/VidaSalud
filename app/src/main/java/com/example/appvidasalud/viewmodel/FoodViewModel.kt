package com.example.appvidasalud.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appvidasalud.data.food.FoodDatabase
import com.example.appvidasalud.data.food.FoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    // Obtenemos la instancia del DAO desde la base de datos
    private val foodDao = FoodDatabase.getDatabase(application).foodDao()

    // Flujo de datos para la lista (ya viene ordenado por el DAO)
    val allEntries: Flow<List<FoodEntry>> = foodDao.getAllEntries()

    /**
     * Función para "Publicar" (guardar en la BD)
     * Detecta la hora y día automáticamente.
     */
    fun saveFoodEntry(title: String, description: String, imageUri: String) {
        viewModelScope.launch {
            val entry = FoodEntry(
                title = title,
                description = description,
                imageUri = imageUri,
                timestamp = System.currentTimeMillis() // ¡Aquí guardamos la fecha y hora actual!
            )
            foodDao.insertEntry(entry)
        }
    }
}

/**
 * Factory para poder pasar el 'Application' context al ViewModel.
 * Es idéntico a tu HealthViewModelFactory.
 */
class FoodViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}