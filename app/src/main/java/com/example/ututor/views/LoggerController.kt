package com.example.ututor.views
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ututor.R
import com.example.ututor.viewadapters.LoggerViewAdapter
import com.example.ututor.viewmodels.LoggerViewModel
import com.example.ututor.databinding.ActivityLogBinding
import androidx.lifecycle.Observer
import com.example.ututor.models.Lesson
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList

class LoggerController : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val  loggerViewModel: LoggerViewModel by viewModels()
    lateinit var university: String
    lateinit var role: String
    lateinit var user: String

    //val logList = ArrayList<ArrayList<*>>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log)
        binding.viewModel = loggerViewModel
        binding.lifecycleOwner = this
        var lesson = intent.getSerializableExtra("Lesson") as Lesson
        university = intent.getSerializableExtra("university") as String
        role = intent.getSerializableExtra("role") as String
        user = intent.getSerializableExtra("user") as String
        loggerViewModel.setCurrentLesson(lesson)
        title = "LOG VIEW"
        val listViewLog = findViewById<ListView>(R.id.resourcesView)
        //val db = Firebase.firestore
        // da sistemare il passaggio di informazioni

        val logsObserver = Observer<ArrayList<*>> { logs ->
            createViewByFilter(logs as ArrayList<ArrayList<*>>,listViewLog)
        }

        loggerViewModel._logList.observe(this,logsObserver)
        loggerViewModel.loadLog(university)

    }

    private fun createViewByFilter(logList: ArrayList<ArrayList<*>>, listViewLog: ListView) {

        val listTimeStampI= getdateI(logList,0)
        val listTimeStampF = getdateF(logList,0)
        val listUser = getdateI(logList,1)

        println("listDateI $listTimeStampI")
        val listDateI: MutableList<String> = convertListDate(listTimeStampI)
        val listDateF: MutableList<String> = convertListDate(listTimeStampF)
        println("listDateF $listDateF")
        println("listUser $listUser")
        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = LoggerViewAdapter(this,
            listDateI,listDateF, listUser)

        listViewLog.adapter = arrayAdapter
    }


    private fun convertListDate(listDateI: MutableList<*>): MutableList<String> {
        val temp: MutableList<String> = arrayListOf()

        for(elm in listDateI){
            temp.add(getDateTime(elm.toString()))
        }
        return temp
    }

    private fun getDateTime(s: String): String {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun getdateI(logList: ArrayList<ArrayList<*>>, i: Int): MutableList<String> {
        var temp: MutableList<String> = ArrayList()
        logList.forEachIndexed { index, element ->
            if(index % 2 == 0){
                temp.add(element[i].toString())
            }

        }
        return temp
    }
    private fun getdateF(logList: ArrayList<ArrayList<*>>, i: Int): MutableList<String> {
        var temp: MutableList<String> = ArrayList()
        logList.forEachIndexed { index, element ->
            if(index % 2 != 0){
                temp.add(element[i].toString())
            }
        }
        return temp
    }
    private fun getListOfInfo(lesson: ArrayList<ArrayList<*>>, i: Int): MutableList<String> {
        var temp: MutableList<String> = ArrayList()
        var s = 0

        for (item in lesson) {
            if (i == 3){
                if(s % 2 == 0)
                    temp.add(item[3] as String)
                else continue
            }
            else {if(s % 2 == 0 && i == 0)
                temp.add(item[0] as String)
                if(s % 2 != 0 && i == 1)
                    temp.add(item[1] as String)
                s=+1
            }
        }
        return temp
    }
}




