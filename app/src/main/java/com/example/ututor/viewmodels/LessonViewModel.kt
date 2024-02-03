package com.example.ututor.viewmodels
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ututor.models.Lesson
import com.example.ututor.models.LessonRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

// context: Activity, private val user: MutableList<String>, private val title: MutableList<String>, private val state: MutableList<String>, private val id: MutableList<String>)
class LessonViewModel() : ViewModel() {
    private var lessonRepo = LessonRepo()
    var _resourcesList = MutableLiveData<ArrayList<String>>()
    var _lessonResource =  MutableLiveData<String>()
    var _state = MutableLiveData<Int>()
    var _currentLesson = MutableLiveData<Lesson>()
    val currentLesson: LiveData<Lesson> = _currentLesson

    fun setLessonList(lessonList: ArrayList<Lesson>){
        lessonRepo.setLessonList(lessonList)
    }

    fun getResources(context: Activity,university: String) {
        viewModelScope.launch {
            lessonRepo.loadRisorse(university) {
                _resourcesList.value = it
            }
        }
    }

    fun getResourceLesson(){
        _lessonResource.value = lessonRepo.getCurrentLesson().getRisorse()
    }

    fun setCurrentLesson(lesson: Lesson){
        lessonRepo.setCurrentLesson(lesson)
    }

    fun logLesson(university: String, message:String, user: String){
        viewModelScope.launch{
            lessonRepo.addLog(university, message, user)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateLesson(university: String, state:String, desc:String, resource: String, name: String, date:String, scadenza:String, inizio: Timestamp?){
        viewModelScope.launch {
            var lesson = lessonRepo.getCurrentLesson()
            lesson.setState(state)
            lesson.setDescizione(desc)
            lesson.setRisorse(resource)
            lesson.setName(name)
            lesson.setData(date)
            lesson.setScadenza(scadenza)
            if (inizio != null) {
                lesson.setOraInizio(inizio)
            }
            lessonRepo.setCurrentLesson(lesson)
            lessonRepo.updateLesson(university)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun freeLesson(university:String, state:String, student:String, totTime: Long){
        viewModelScope.launch {
            var lesson = lessonRepo.getCurrentLesson()
            lesson.setState(state)
            lesson.setStudent(student)
            lesson.setTempoTotale(totTime)
            lessonRepo.setCurrentLesson(lesson)
            lessonRepo.updateLesson(university)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun doneLesson(university:String, state:String,  totTime: Long){
        viewModelScope.launch {
            var lesson = lessonRepo.getCurrentLesson()
            lesson.setState(state)
            lesson.setTempoTotale(totTime)
            lessonRepo.setCurrentLesson(lesson)
            lessonRepo.updateLesson(university)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startLesson(university:String, state:String, student: String,  startTime: Timestamp){
        viewModelScope.launch {
            var lesson = lessonRepo.getCurrentLesson()
            lesson.setState(state)
            lesson.setOraInizio(startTime)
            lessonRepo.setCurrentLesson(lesson)
            lessonRepo.updateLesson(university)
        }
    }

    fun deleteLesson(university: String){
        viewModelScope.launch {
            lessonRepo.deleteLesson(university)
        }
    }

    fun checkState(university:String){
        viewModelScope.launch {
            lessonRepo.checkState(university) {
                _state.value = it
            }
        }
    }

    fun onCreate(){
        _currentLesson.value = lessonRepo.getCurrentLesson()
    }



}



