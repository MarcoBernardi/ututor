package com.example.ututor

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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import com.example.ututor.models.UserModel

class Login : AppCompatActivity() {
    @SuppressLint("SetTextI18n")

    var stateLogin = "error"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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

        val settings = applicationContext.getSharedPreferences("ututor", 0)
        val editor = settings.edit()
        val UM = UserModel()
        val db = Firebase.firestore
        val azienda = findViewById<EditText>(R.id.azienda)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.button)
        if ((this@Login.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo == null) {
            Toast.makeText(this@Login, "No Internet Connection", Toast.LENGTH_SHORT).show()

        }
        buttonLogin.setOnClickListener(View.OnClickListener {
            if ((this@Login.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo == null)
                Toast.makeText(this@Login, "No Internet Connection", Toast.LENGTH_SHORT).show()
            else {
                val intent = Intent(this@Login, Dashboard::class.java)
                if (!azienda.text.isEmpty() || !username.text.isEmpty() || !password.text.isEmpty()) {
                    UM.login(this@Login,
                        azienda.text.toString(),
                        username.text.toString(),
                        password.text.toString()
                    ) {
                        if (!it.equals("error")) {
                            Toast.makeText(this@Login, it.toString(), Toast.LENGTH_SHORT).show()

                            editor.putString("role", it)
                            editor.putString("university", azienda.text.toString())
                            editor.putString("user", username.text.toString())
                            if (findViewById<Switch>(R.id.saveData).isChecked)
                                applicationContext.openFileOutput("ututor_data", Context.MODE_PRIVATE)
                                    .use {
                                        val vw =
                                            "university=" + azienda.text.toString() + "\nuser=" + username.text.toString() + "\npassword=" + password.text.toString()
                                        it.write(vw.toByteArray())
                                    }
                            else
                                applicationContext.deleteFile("ututor_data")
                            editor.apply()
                            startActivity(intent)

                        } else {
                            azienda.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                            username.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))
                            password.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#FC2D00"))

                        }
                    }

                } else {
                    azienda.backgroundTintList =
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


