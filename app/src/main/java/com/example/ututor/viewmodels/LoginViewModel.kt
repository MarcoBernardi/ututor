package com.example.ututor.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ututor.models.UserRepo
import com.example.ututor.room.AppDatabase
import com.example.ututor.room.Preferences
import com.example.ututor.room.PreferencesDAO
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private var userRepo = UserRepo()
    lateinit var preferencesDb: PreferencesDAO

    fun insertPreference(university: String, role: String, username: String){
        viewModelScope.launch {

            var preferences = Preferences(university = university, username = username, role = role)
            preferencesDb.insertAll(preferences)
        }
    }

    fun getRoomDb(context:Context) {
        preferencesDb = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "preferences"
        ).build().preferencesDAO()
    }

    fun login(university: String, username: String, password: String,  myCallback: (result: String) -> Unit){

        viewModelScope.launch {
            userRepo.login(university, username, password){
                myCallback.invoke(it)
            }
        }


    }
}