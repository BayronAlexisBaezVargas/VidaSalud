package com.example.appvidasalud.data.food

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_log")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val imageUri: String, // Guardamos la URI de la imagen como un String
    val timestamp: Long  // Para guardar la fecha y hora exactas
)