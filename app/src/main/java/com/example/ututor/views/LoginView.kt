package com.example.ututor.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ututor.R
import com.example.ututor.databinding.ActivityLoginBinding
import java.io.File
import com.example.ututor.viewmodels.LoginViewModel

class LoginView : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    @SuppressLint("SetTextI18n")
    var stateLogin = "error"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this
        title = "UTutor"
        val file = File(applicationContext.filesDir, "ututor_data")
        if(file.isFile)
            applicationContext.openFileInput("ututor_data").bufferedReader().useLines { lines ->
                for(l in lines){
                    val v = l.split("=")
                    findViewById<EditText>(resources.getIdentifier(v[0], "id", packageName)).setText(v[1])
                    findViewById<Switch>(R.id.saveData).isChecked = true
                }
            }


        loginViewModel.getRoomDb(applicationContext)
        val university = findViewById<EditText>(R.id.university)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.button)
        if ((this@LoginView.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo == null) {
            Toast.makeText(this@LoginView, "No Internet Connection", Toast.LENGTH_SHORT).show()

        }
        buttonLogin.setOnClickListener(View.OnClickListener {
            if ((this@LoginView.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo == null)
                Toast.makeText(this@LoginView, "No Internet Connection", Toast.LENGTH_SHORT).show()
            else {
                val intent = Intent(this@LoginView, DashboardView::class.java)
                if (!university.text.isEmpty() || !username.text.isEmpty() || !password.text.isEmpty()) {
                    loginViewModel.login(university.text.toString(),
                                        username.text.toString(),
                                        password.text.toString())
                    {
                        if (!it.equals("error")) {
                            loginViewModel.insertPreference( university.text.toString(), it,username.text.toString())
                            if (findViewById<Switch>(R.id.saveData).isChecked)
                                applicationContext.openFileOutput("ututor_data", Context.MODE_PRIVATE)
                                    .use {
                                        val vw =
                                            "university=" + university.text.toString() + "\nuser=" + username.text.toString() + "\npassword=" + password.text.toString()
                                        it.write(vw.toByteArray())
                                    }
                            else
                                applicationContext.deleteFile("ututor_data")
                            startActivity(intent)

                        } else {
                            university.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                            username.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                            password.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))

                        }
                    }

                } else {
                    university.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                    username.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                    password.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FC2D00"))

                }
            }


        })
    }



}


