package com.example.ututor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ututor.models.LessonRepo
import kotlinx.coroutines.launch

class NewLessonViewModel: ViewModel() {
    private var lessonRepo = LessonRepo()

    var _resourcesList = MutableLiveData<ArrayList<String>>()
    private var resourceList: LiveData<ArrayList<String>> = _resourcesList


    fun getResources(university:String){
        viewModelScope.launch {
            lessonRepo.loadRisorse(university) {
                _resourcesList.value = it
            }
        }
    }

    fun addLesson(university: String, lesson: HashMap<String,*>) {
        viewModelScope.launch {
            lessonRepo.addLesson(university, lesson)
        }
    }

    fun onCreate(){

    }




}
