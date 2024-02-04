package com.example.ututor.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ututor.models.Lesson
import androidx.lifecycle.Observer

import com.example.ututor.R
import com.example.ututor.databinding.ActivityDashboardBinding
import com.example.ututor.models.LessonRepo
import com.example.ututor.room.Preferences
import com.example.ututor.viewadapters.LessonViewAdapter
import com.example.ututor.viewmodels.DashboardViewModel
import com.google.android.material.chip.Chip
import java.util.*
import kotlin.collections.ArrayList


class Dashboard : AppCompatActivity() {
    lateinit var listView: ListView
    private lateinit var binding: ActivityDashboardBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val tM = LessonRepo()
    lateinit var university:String
    lateinit var role: String
    lateinit var user: String
    lateinit var lessonsData: ArrayList<Lesson>
    lateinit var lessonsDataAll: ArrayList<Lesson>
    lateinit var lessonsDataFree: ArrayList<Lesson>
    lateinit var lessonsDataComplete: ArrayList<Lesson>
    lateinit var lessonsDataProgress: ArrayList<Lesson>
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager


    fun handleLesson(context: Context, result: androidx.activity.result.ActivityResult){

        val valueId = findById(result.data!!.getStringExtra("value") as String)
        if(valueId != null) {
            dashboardViewModel.setCurrentLesson(valueId)
            val intent = Intent(context, LessonController::class.java)
            context.startActivity(intent)
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
           handleLesson(this@Dashboard, result)
        }
        else
            Toast.makeText(this, "Lezione non trovata", Toast.LENGTH_SHORT).show()
    }




    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.setGroupVisible(R.id.updateResources,false)
        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addLesson ->      {
                val intent = Intent(this@Dashboard, NewLessonController::class.java)
                intent.putExtra("university", university)
                intent.putExtra("role", role)
                intent.putExtra("user", user)
                startActivity(intent)
                return true
            }   //add the function to perform here

            R.id.updateResources ->      {
                val intent = Intent(this@Dashboard, ResourcesController::class.java)
                intent.putExtra("LessonList", dashboardViewModel.getLessonList())
                intent.putExtra("university", university)
                intent.putExtra("role", role)
                intent.putExtra("user", user)
                startActivity(intent)
                return true
            }   //add the function to perform here

            R.id.exit ->   {

                finish()
                dashboardViewModel.deletePreferences()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()


    }

    fun setNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "UTutor"
            val descriptionText ="UTutor Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            notificationChannel = NotificationChannel("i.apps.notifications", descriptionText, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            val channel = NotificationChannel("i.apps.notifications", name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this
        //dashboardViewModel.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setNotification()
        //Load lessons for the first time
        listView = findViewById<ListView>(R.id.JobList)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                    listView.isEnabled = true
                }
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                Toast.makeText(this@Dashboard,"Please enable internet connectivity for the app to work!",
                    Toast.LENGTH_LONG ).show()
                runOnUiThread { listView.isEnabled = false}
            }
        })

        val preferencesObserver = Observer<Preferences> { preferences ->
            role = preferences.role.toString()
            university = preferences.university.toString()
            user = preferences.username.toString()
            dashboardViewModel.refreshLessons()

        }
        //dashboardViewModel.refreshLessons()
        dashboardViewModel._preferences.observe(this, preferencesObserver)
        dashboardViewModel.setPreferences(applicationContext)


        val dashBoardObserver = Observer<ArrayList<Lesson>> { lessons ->
            lessonsData = lessons
            filter(lessonsData)
            createViewByFilter()
        }
        //dashboardViewModel.refreshLessons()
        dashboardViewModel.lessonData.observe(this, dashBoardObserver)
        setUpFilters(listView)
        setupListeners(listView)

    }

    private fun filter(lessons: ArrayList<Lesson>) {
        val all = ArrayList<Lesson>()
        val free = ArrayList<Lesson>()
        val progress = ArrayList<Lesson>()
        val complete = ArrayList<Lesson>()
        for(item in lessons){
            all.add(item)
            if(item.getState() == "libero")
                free.add(item)
            if(item.getState() == "in corso")
                progress.add(item)
            if(item.getState() == "concluso")
                complete.add(item)
        }
        lessonsDataAll = all
        lessonsDataFree = free
        lessonsDataProgress = progress
        lessonsDataComplete = complete
    }

    private fun createViewByFilter(filteredLessons: ArrayList<Lesson> = lessonsData) {
        val arrayAdapter: ArrayAdapter<*>
        val listTitle = getListOfInfo(0, filteredLessons)
        val listState = getListOfInfo(1, filteredLessons)
        val listStudent = getListOfInfo(2, filteredLessons)
        val listId = getListOfInfo(3, filteredLessons)
        arrayAdapter = LessonViewAdapter(this,
            listStudent,listTitle, listState, listId)

        listView.adapter = arrayAdapter
    }

    private fun getListOfInfo( i: Int, filteredLessons: ArrayList<Lesson>): MutableList<String> {
        val temp: MutableList<String> = java.util.ArrayList()
        when(i){
            0 -> {
                for (item in filteredLessons)
                    temp.add(item.getName())
            }
            1 -> {
                for (item in filteredLessons)
                    temp.add(item.getState())
            }
            2 -> {
                for (item in filteredLessons)
                    temp.add(item.getStudent())
            }
            3 -> {
                for (item in filteredLessons)
                    temp.add(item.getId())
            }
        }
        return temp
    }


    fun setupListeners(listView:ListView){
        val swipeToRefreshLV = findViewById<SwipeRefreshLayout>(R.id.idSwipeToRefresh)
        swipeToRefreshLV.setOnRefreshListener {
            swipeToRefreshLV.isRefreshing = false
            swipeToRefreshLV.setColorSchemeColors(Color.RED)
            val allChip =findViewById<Chip>(R.id.all)
            allChip.isChecked = true
            allChip.callOnClick()
        }
        listView.setOnItemClickListener() { adapterView, _, position, _ ->
            val id = listView.getItemAtPosition(position)
            val lesson = findById(id as String)
            val intent = Intent(this, LessonController::class.java)
            if (lesson != null) {
                dashboardViewModel.setCurrentLesson(lesson)
            }
            intent.putExtra("Lesson", lesson)
            intent.putExtra("LessonList", dashboardViewModel.getLessonList())
            intent.putExtra("university", university)
            intent.putExtra("role", role)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    fun findById(id: String): Lesson? {
        for (item in lessonsData) {
            if(item.getId()==id){
                return item
            }
        }
        return null
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if (menu != null) {
            menu.findItem(R.id.addLesson).isVisible = role == "professor"
        }
        if (menu != null) {
            menu.findItem(R.id.updateResources).isVisible = role == "professor"
        }
        return true
    }

    fun checkFilter(switchStatus: MutableList<Boolean>): ArrayList<Lesson> {

        var temp = ArrayList<Lesson>()
        if (switchStatus[0])
            temp = lessonsData
        if (switchStatus[1])
            for (i in lessonsDataFree) {
                temp.add(i)
            }
        if (switchStatus[2])
            for (i in lessonsDataProgress) {
                temp.add(i)
            }
        if (switchStatus[3])
            for (i in lessonsDataComplete) {
                temp.add(i)
            }
        return temp

    }



    fun setUpFilters(listView:ListView){
        val chip1 = findViewById<Chip>(R.id.all)
        val chip2 = findViewById<Chip>(R.id.free)
        val chip3 = findViewById<Chip>(R.id.running)
        val chip4 = findViewById<Chip>(R.id.finished)
        val switchStatus: MutableList<Boolean> = mutableListOf(true, false, false, false)

        chip1.setOnClickListener {
            switchStatus[0] = chip1.isChecked
            //println("prima$switchStatus")
            if (chip1.isChecked) {
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            } else {
                if (!switchStatus[1] && !switchStatus[2] && !switchStatus[3]) {
                    chip1.isChecked = true
                }
            }
            switchStatus[0] = chip1.isChecked
            switchStatus[1] = chip2.isChecked
            switchStatus[2] = chip3.isChecked
            switchStatus[3] = chip4.isChecked
            val filteredLesson = checkFilter(switchStatus)
            createViewByFilter(filteredLesson)

        }

        chip2.setOnClickListener {
            switchStatus[1] = chip2.isChecked

            if (chip2.isChecked) {
                chip1.isChecked = false
            }
            if (switchStatus[1] && switchStatus[2] && switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }
            if (!switchStatus[1] && !switchStatus[2] && !switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }

            switchStatus[0] = chip1.isChecked
            switchStatus[1] = chip2.isChecked
            switchStatus[2] = chip3.isChecked
            switchStatus[3] = chip4.isChecked
            val filteredLesson = checkFilter(switchStatus)
            createViewByFilter(filteredLesson)
        }

        chip3.setOnClickListener {
            switchStatus[2] = chip3.isChecked
            if (chip3.isChecked) {
                chip1.isChecked = false
            }


            if (switchStatus[1] && switchStatus[2] && switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }
            if (!switchStatus[1] && !switchStatus[2] && !switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }
            switchStatus[0] = chip1.isChecked
            switchStatus[1] = chip2.isChecked
            switchStatus[2] = chip3.isChecked
            switchStatus[3] = chip4.isChecked

            val filteredLesson = checkFilter(switchStatus)
            createViewByFilter(filteredLesson)
        }

        chip4.setOnClickListener {
            switchStatus[3] = chip4.isChecked
            if (chip4.isChecked) {
                chip1.isChecked = false
            }
            if (switchStatus[1] && switchStatus[2] && switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }
            if (!switchStatus[1] && !switchStatus[2] && !switchStatus[3]) {
                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
            }
            switchStatus[0] = chip1.isChecked
            switchStatus[1] = chip2.isChecked
            switchStatus[2] = chip3.isChecked
            switchStatus[3] = chip4.isChecked

            val filteredLesson = checkFilter(switchStatus)
            createViewByFilter(filteredLesson)
        }
    }






}
