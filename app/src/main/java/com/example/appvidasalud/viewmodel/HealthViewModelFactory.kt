package com.example.appvidasalud.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Esta clase es un "molde" que le dice a Android cómo crear tu HealthViewModel,
// ya que ahora necesita un parámetro (Application) en su constructor.
class HealthViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}