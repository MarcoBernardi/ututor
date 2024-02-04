package com.example.ututor.views
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.ututor.models.Lesson
import com.example.ututor.R
import com.example.ututor.databinding.ActivityResourcesBinding
import com.example.ututor.viewadapters.ResourcesViewAdapter
import com.example.ututor.viewmodels.ResourcesViewModel


class ResourcesView : AppCompatActivity() {

    private lateinit var binding: ActivityResourcesBinding
    private val resourcesViewModel: ResourcesViewModel by viewModels()
    var value = ""
    lateinit var user : String
    lateinit var university: String
    lateinit var role: String

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        menuInflater.inflate(R.menu.menu_resource, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.delete_resource) {
            if (value != "") {
                resourcesViewModel.deleteResource(university, value)
                resourcesViewModel.loadRisorse(university)

            }
        }
        if (id == R.id.add_resource) {
            var addRis = ""

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Nome Risorsa")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(
                "Aggiungi"
            ) { dialog, which -> addRis = input.text.toString()
                resourcesViewModel.addResource(university,addRis)
                resourcesViewModel.loadRisorse(university)
            }


            builder.show()


        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resources)
        binding.viewModel = resourcesViewModel
        binding.lifecycleOwner = this
        title = "GESTIONE RISORSE"
        val lessonList = intent.getSerializableExtra("LessonList") as ArrayList<Lesson>
        university = intent.getSerializableExtra("university") as String
        role = intent.getSerializableExtra("role") as String
        user = intent.getSerializableExtra("user") as String

        resourcesViewModel.setLessonList(lessonList)
        val listViewRes = findViewById<ListView>(R.id.resourcesView)
        var arrayAdapter: ArrayAdapter<*>

        val resourcesObserver = Observer<ArrayList<String>> { res ->
            res.removeAt(0)
            arrayAdapter = ResourcesViewAdapter(this, res)
            listViewRes.adapter = arrayAdapter
        }

        resourcesViewModel.loadRisorse(university)
        resourcesViewModel._resources.observe(this, resourcesObserver)

        listViewRes.setOnItemClickListener() { arrayAdapter, view, position, _ ->
            value =  listViewRes.getItemAtPosition(position) as String
            println(value)

        }

    }


}

