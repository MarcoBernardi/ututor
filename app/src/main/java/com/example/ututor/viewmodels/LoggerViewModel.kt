package com.example.ututor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ututor.models.Lesson
import com.example.ututor.models.LessonRepo
import kotlinx.coroutines.launch

class LoggerViewModel: ViewModel() {
    private var lessonRepo = LessonRepo()
    var _logList = MutableLiveData<ArrayList<*>>()
    var  logList : LiveData<ArrayList<*>> = _logList

    fun setCurrentLesson(lesson: Lesson){
        lessonRepo.setCurrentLesson(lesson)
    }

    fun loadLog(university:String){
        viewModelScope.launch {
            lessonRepo.loadLog(university) {
                _logList.value = it
            }
        }
    }
}
