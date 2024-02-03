package com.example.ututor.room

import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity
data class Preferences(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "university") val university: String?,
    @ColumnInfo(name = "user") val role: String?,
    @ColumnInfo(name = "role") val username: String?
)


@Database(entities = [Preferences::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun preferencesDAO(): PreferencesDAO
}