package com.example.ututor.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ututor.models.Lesson
import com.example.ututor.models.LessonRepo
import kotlinx.coroutines.launch

class ResourcesViewModel: ViewModel() {
    private var lessonRepo = LessonRepo()
    var _resources = MutableLiveData<ArrayList<String>>()
    var resources: LiveData<ArrayList<String>> = _resources
    fun setLessonList(lessons: ArrayList<Lesson>){
        lessonRepo.setLessonList(lessons)
    }

    fun loadRisorse(university: String){
        viewModelScope.launch {
            lessonRepo.loadRisorse(university) {
                _resources.value = it
            }
        }
    }

    fun deleteResource(university:String, resource:String ) {
        viewModelScope.launch {
            lessonRepo.deleteRisorse(university, resource)
        }
    }

    fun addResource(university: String, resource: String){
        viewModelScope.launch {
            lessonRepo.addRisorse(university, resource)
        }
    }

}