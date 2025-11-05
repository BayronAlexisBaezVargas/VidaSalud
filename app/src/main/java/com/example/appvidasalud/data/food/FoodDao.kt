package com.example.appvidasalud.data.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Insert
    suspend fun insertEntry(entry: FoodEntry)

    // Query que cumple tu requisito: "ordenada... de la fecha mas antigua a la más nueva"
    @Query("SELECT * FROM food_log ORDER BY timestamp ASC")
    fun getAllEntries(): Flow<List<FoodEntry>>

}