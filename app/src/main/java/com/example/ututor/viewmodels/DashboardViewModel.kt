package com.example.ututor.viewmodels
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ututor.models.Lesson
import com.example.ututor.models.LessonRepo
import com.example.ututor.room.AppDatabase
import com.example.ututor.room.PreferencesDAO
import kotlinx.coroutines.launch
import com.example.ututor.room.Preferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class DashboardViewModel()
    : ViewModel() {

    private var lessonRepo = LessonRepo()
    var _preferences = MutableLiveData<Preferences>()
    private var preferences: LiveData<Preferences> = _preferences
    var _lessonData = MutableLiveData<ArrayList<Lesson>>()
    var lessonData: LiveData<ArrayList<Lesson>> = _lessonData
    var un = ""
    var us = ""
    var ro = ""

    lateinit var settings: SharedPreferences
    lateinit var preferencesDb: PreferencesDAO

    fun setPreferences(context: Context){

        getRoomDb(context)
        var pref: Preferences? = null
        pref =  preferencesDb.getAll()[0]
        un = pref.university.toString()
        us = pref.role.toString()
        ro = pref.username.toString()
        _preferences.value = pref

    }

    fun getUniversity(): String {
        return un
    }

    fun getRole(): String{
        return ro
    }
    fun getUser(): String{
        return us
    }


    private fun getRoomDb(context: Context) {
        preferencesDb = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "preferences"
        ).allowMainThreadQueries().build().preferencesDAO()
    }

    fun onCreate(){
        //this.settings = settings
    }

    fun refreshLessons(){
        //var university = this.settings.getString("university", "error").toString()
        //var role = this.settings.getString("role", "error").toString()
        var professor: String? = null
        if (ro == "professor"){
            professor = us
        }
        viewModelScope.launch {
            lessonRepo.load(un, professor) {
                _lessonData.value = it
            }
        }
    }

    fun getLessonList():ArrayList<Lesson>{
        return lessonRepo.getLessonList()
    }

    fun setCurrentLesson(lesson: Lesson) {
        lessonRepo.setCurrentLesson(lesson)
    }

}