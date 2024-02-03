package com.example.ututor.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface PreferencesDAO {

    @Query("SELECT * FROM preferences")
    fun getAll(): List<Preferences>

    @Insert
    suspend fun insertAll(vararg preference : Preferences)

    @Query("SELECT university FROM preferences")
    fun getUniversity(): String

    @Query("SELECT role FROM preferences")
    fun getRole(): String

    @Query("SELECT user FROM preferences")
    fun getUser(): String



}