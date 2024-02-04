package com.example.ututor.views
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.ututor.R
import com.example.ututor.databinding.ActivityNewLessonBinding
import com.example.ututor.viewmodels.NewLessonViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewLessonView : AppCompatActivity() {
    private lateinit var binding: ActivityNewLessonBinding
    private val newLessonViewModel: NewLessonViewModel by viewModels()
    lateinit var university: String
    lateinit var role: String
    lateinit var user: String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_lesson)
        binding.viewModel = newLessonViewModel
        binding.lifecycleOwner = this
        title = "LEZIONE"
        val db = Firebase.firestore

        val buttonCrea = findViewById<Button>(R.id.crea)
        val lessonName = findViewById<EditText>(R.id.lezioneNome)
        val description = findViewById<EditText>(R.id.descrizione)
        val dateEdt = findViewById<EditText>(R.id.scadenza)
        val risorse = findViewById<Spinner>(R.id.risorse)
        university = intent.getSerializableExtra("university") as String
        role = intent.getSerializableExtra("role") as String
        user = intent.getSerializableExtra("user") as String

        //val TM = intent.getSerializableExtra("Model") as LessonModel


        dateEdt.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    dateEdt.setText(dat)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        buttonCrea.setOnClickListener(View.OnClickListener {
            //println(verifyId(azienda.text.toString(),username.text.toString(),password.text.toString()))

            val intent = Intent(this@NewLessonView, DashboardView::class.java)


            var temp = ""
            if (risorse.selectedItem.toString() == "Default - None")
                temp = "None"
            else
                temp = risorse.selectedItem.toString()
            val lesson = hashMapOf(
                "nome" to lessonName.text.toString(),
                "data" to Date(),
                "descrizone" to description.text.toString(),
                "risorse" to temp,
                "scadenza" to SimpleDateFormat("dd/MM/yyyy").parse(dateEdt.text.toString()),
                "stato" to "libero",
                "tempoTotale" to 0,
                "student" to "",
                "professor" to user,
                "oraInizio" to Timestamp(Date())
            )

            newLessonViewModel.addLesson(university, lesson )

            finish()
        })

        val resourcesObserver = Observer<ArrayList<String>> { resources ->
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            risorse!!.adapter = aa
        }

        newLessonViewModel._resourcesList.observe(this, resourcesObserver)

        newLessonViewModel.getResources(university)


    }
}