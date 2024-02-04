package com.example.ututor.views
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import androidx.core.view.isVisible
import com.example.ututor.Allert
import com.example.ututor.models.Lesson
import com.example.ututor.R
import com.example.ututor.databinding.ActivityLessonBinding
import com.example.ututor.viewmodels.LessonViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LessonView : AppCompatActivity() {

    private lateinit var binding: ActivityLessonBinding
    private val lessonViewModel: LessonViewModel by viewModels()
    lateinit var university: String
    lateinit var role: String
    lateinit var user: String




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lesson, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lesson)
        binding.viewModel = lessonViewModel
        binding.lifecycleOwner = this
        var resources = ArrayList<String>()
        title = "DASHBOARD LEZIONI"
        university = intent.getSerializableExtra("university") as String
        role = intent.getSerializableExtra("role") as String
        user = intent.getSerializableExtra("user") as String

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, object :
            ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                    enabledElementInternateState(
                        true,
                        role == "professor"
                    )
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Toast.makeText(
                    applicationContext,
                    "L'applicazione per essere utilizzata ha bisogno di intenet",
                    Toast.LENGTH_LONG
                ).show()
                runOnUiThread {
                    enabledElementInternateState(
                        false,
                        role == "professor"
                    )
                }
            }
        })

        var lesson = intent.getSerializableExtra("Lesson") as Lesson
        val lessonList = intent.getSerializableExtra("LessonList") as ArrayList<Lesson>
        var work = lesson
        lessonViewModel.setCurrentLesson(lesson)
        lessonViewModel.setLessonList(lessonList)
        lessonViewModel.onCreate()


        val selectedResourceObserver = Observer<String> { res ->
            findViewById<Spinner>(R.id.risorsaSpinner).setSelection(resources.indexOf(res))
            if (role == "professor") {
                setElement(true)
            }
            else{
                setElement(false)
            }
        }

        val resourcesObserver = Observer<ArrayList<String>> { res ->
            val risorsaAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, res)
            risorsaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            findViewById<Spinner>(R.id.risorsaSpinner).adapter = risorsaAdapter
            resources = res
            lessonViewModel._lessonResource.observe(this, selectedResourceObserver)
        }


        lessonViewModel.getResources(this, university)
        lessonViewModel._resourcesList.observe(this, resourcesObserver)
        lessonViewModel.getResourceLesson()

        /// STATO
        val stateSpinner = findViewById<Spinner>(R.id.stateSpinner)
        val stateList = mutableListOf<String>("libero", "in corso", "concluso")
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = aa
        stateSpinner.setSelection(stateList.indexOf(lessonViewModel.currentLesson.value?.getState()))


        // Cronometro
        val meter = Chronometer(this)
        meter.setTextColor(meter.context.resources.getColor(R.color.purple_500))
        meter.setTextSize(TypedValue.COMPLEX_UNIT_IN, 0.3f)
        meter.setText("")

        findViewById<EditText>(R.id.data).setText(
            SimpleDateFormat("dd/MM/yyyy").format(
                Date(
                    work.getData().toLong() * 1000
                )
            )
        )
        findViewById<EditText>(R.id.deadline).setText(
            SimpleDateFormat("dd/MM/yyyy").format(
                Date(
                    work.getScadenza().toLong() * 1000
                )
            )
        )
        work.setData(findViewById<EditText>(R.id.data).text.toString())
        work.setScadenza(findViewById<EditText>(R.id.deadline).text.toString())
        //----------
        var timeWhenStopped: Long = 0
        var bb: Boolean = false

        val lessonStateObserver = Observer<Lesson> { lesson ->
            when (lesson.getState()) {
                "in corso" -> {
                    findViewById<Button>(R.id.start_button).isVisible = false
                    findViewById<Button>(R.id.done_button).isVisible = true
                    findViewById<Button>(R.id.modify_button).isVisible = false
                    meter.base =
                        SystemClock.elapsedRealtime() - ((Timestamp.now().seconds - lesson.getoraInzio()) * 1000 + lesson.gettompoTotale())
                    meter.start()
                }
                "concluso" -> {
                    findViewById<Button>(R.id.start_button).isVisible = false
                    findViewById<Button>(R.id.done_button).isVisible = false
                    findViewById<Button>(R.id.modify_button).isVisible = false
                    meter.base = SystemClock.elapsedRealtime() -lesson.gettompoTotale()
                }
                "libero" -> {
                    meter.base = SystemClock.elapsedRealtime() - lesson.gettompoTotale()
                    bb = true
                }
            }
        }

        lessonViewModel._currentLesson.observe(this, lessonStateObserver)


        val linearLayout = findViewById<LinearLayout>(R.id.VisTimer)
        linearLayout?.addView(meter)
        val stateObserver = Observer<Int> { state ->
            if (state == 0) {
                if (bb) {
                    meter.base =
                        SystemClock.elapsedRealtime() - lessonViewModel.currentLesson.value?.gettompoTotale()!!
                    meter.start()
                    bb = false
                } else {
                    meter.base = SystemClock.elapsedRealtime() + timeWhenStopped
                    meter.start()
                }
                findViewById<Spinner>(R.id.stateSpinner).setSelection(
                    stateList.indexOf(
                        "in corso"
                    )
                )
                findViewById<Button>(R.id.start_button).isVisible = false
                findViewById<Button>(R.id.modify_button).isVisible = false
                lessonViewModel.startLesson(university, "in corso", user, Timestamp.now())
                lessonViewModel.logLesson(university, "Inizio lezione", user)
            }
            else {
                when (state) {

                    1 -> Toast.makeText(
                        applicationContext,
                        "Lezione  già iniziata",
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }


        val activityContext = this
        var start = findViewById<Button>(R.id.start_button)
        start.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(v: View) {
                if (work.getState() == "in corso") {
                    meter.stop()
                    timeWhenStopped = meter.base - SystemClock.elapsedRealtime()
                    findViewById<Spinner>(R.id.stateSpinner).setSelection(stateList.indexOf("libero"))
                    findViewById<Button>(R.id.start_button).setText("Inizia")
                    findViewById<Button>(R.id.modify_button).isVisible = false
                    lessonViewModel.freeLesson(university, "libero", "", (SystemClock.elapsedRealtime() - meter.base) )
                    lessonViewModel.logLesson(university, "Lezione Terminata", user) //TODO sbloccare alla fine!!
                } else
                {
                    lessonViewModel.checkState(university)
                }
            }
        })
        lessonViewModel._state.observe(activityContext, stateObserver)

        findViewById<Button>(R.id.done_button).setOnClickListener(View.OnClickListener {
            Allert(this).show(
                "Terminare",
                "Questa operazione conclude la lezione corrente "
            ) {
                if (it.toString() == "YES") {
                    meter.stop()
                    findViewById<Spinner>(R.id.stateSpinner).setSelection(stateList.indexOf("concluso"))
                    findViewById<Button>(R.id.done_button).isVisible = false
                    findViewById<Button>(R.id.start_button).isVisible = false
                    findViewById<Button>(R.id.modify_button).isVisible = false
                    lessonViewModel.doneLesson(university, "concluso",(SystemClock.elapsedRealtime() - meter.base) )
                    lessonViewModel.logLesson(university, "Lezione Terminata", user)
                } else {
                    Toast.makeText(applicationContext, "Conlusione Revocata", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        findViewById<Button>(R.id.modify_button).setOnClickListener(View.OnClickListener {
            Allert(this).show(
                "Sei sicuro di volere Modificare questa lezione?",
                "Attenzione questa azione è irreversibile"
            ) {
                if (it.toString() == "YES") {
                    meter.stop()
                    val state = findViewById<Spinner>(R.id.stateSpinner).selectedItem.toString()
                    val desc = findViewById<EditText>(R.id.description).text.toString()
                    val resource = findViewById<Spinner>(R.id.risorsaSpinner).selectedItem.toString()
                    val name = findViewById<EditText>(R.id.ProjectName).text.toString()
                    val date = findViewById<EditText>(R.id.data).text.toString()
                    val scadenza = findViewById<EditText>(R.id.deadline).text.toString()
                    if (findViewById<Spinner>(R.id.stateSpinner).selectedItem.equals("in corso")) {
                        lessonViewModel.currentLesson.value?.setOraInizio(Timestamp.now())
                        lessonViewModel.updateLesson(university,  state, desc, resource, name, date, scadenza,Timestamp.now())
                    }
                    else
                        lessonViewModel.updateLesson(university, state, desc, resource, name, date, scadenza, null)

                    Toast.makeText(
                        applicationContext,
                        "La modifica è stata effettuata",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Modifica annullato", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        findViewById<Button>(R.id.delete_button).setOnClickListener(View.OnClickListener {
            Allert(this).show(
                "Sei sicuro di volere Eliminare questa lezione?",
                "Attenzione questa azione è irreversibile"
            ) {
                if (it.toString() == "YES") {
                    lessonViewModel.deleteLesson(university)
                    Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Delete annullato", Toast.LENGTH_LONG).show()
                }
            }
        })

        var log = findViewById<Button>(R.id.log_button)
        log.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(this@LessonView, LoggerView::class.java)
                intent.putExtra("Lesson", lessonViewModel._currentLesson.value)
                intent.putExtra("university", university)
                intent.putExtra("role", role)
                intent.putExtra("user", user)
                startActivity(intent)
            }
        })
    }

    fun setElement(valore :Boolean){
        findViewById<Button>(R.id.modify_button).isVisible = valore
        findViewById<Button>(R.id.delete_button).isVisible = valore
        findViewById<EditText>(R.id.ProjectName).isEnabled = valore
        findViewById<Spinner>(R.id.stateSpinner).isEnabled = valore
        findViewById<EditText>(R.id.description).isEnabled = valore
        findViewById<Spinner>(R.id.risorsaSpinner).isEnabled = valore
        findViewById<EditText>(R.id.data).isEnabled = valore
        findViewById<EditText>(R.id.deadline).isEnabled = valore
    }

    fun enabledElementInternateState(valore :Boolean, role: Boolean){
        findViewById<Button>(R.id.log_button).isEnabled = valore
        findViewById<Button>(R.id.start_button).isEnabled = valore
        findViewById<Button>(R.id.done_button).isEnabled = valore
        findViewById<Button>(R.id.modify_button).isEnabled = valore && role
        findViewById<Button>(R.id.delete_button).isEnabled = valore && role
        findViewById<EditText>(R.id.ProjectName).isEnabled = valore && role
        findViewById<Spinner>(R.id.stateSpinner).isEnabled = valore && role
        findViewById<EditText>(R.id.description).isEnabled = valore && role
        findViewById<Spinner>(R.id.risorsaSpinner).isEnabled = valore && role
        findViewById<EditText>(R.id.data).isEnabled = valore && role
        findViewById<EditText>(R.id.deadline).isEnabled = valore && role
    }
}

