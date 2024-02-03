package com.example.ututor.views
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.ututor.R
import com.example.ututor.models.UserRepo
import java.io.File

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilo)
        val UM = UserRepo()
        var pwd: String
        val file = File(applicationContext.filesDir, "ututor_data")
        val buttonUpdate = findViewById<Button>(R.id.changePassword)
        val oldPwdText = findViewById<EditText>(R.id.oldPassword)
        val newPwdText = findViewById<EditText>(R.id.newPassword)
        val settings = applicationContext.getSharedPreferences("ututor", 0)
        val  university = settings.getString("university" , "error").toString()
        val  usr = settings.getString("user" , "error").toString()

        val oldPwd = oldPwdText.text
        val newPwd = newPwdText.text
        buttonUpdate.setOnClickListener {
            UM.updatePwd(this@Profile, university, usr, oldPwd.toString(),newPwd.toString()) { it ->
                if (it) {
                    if (file.isFile) {
                        applicationContext.openFileOutput("ututor_data", Context.MODE_PRIVATE).use {
                            val vw = "university=$university\nuser=$usr\npassword=$newPwd"
                            it.write(vw.toByteArray())
                        }
                        Toast.makeText(applicationContext, "Password modificata", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@Profile, Dashboard::class.java)
                        startActivity(intent)
                    }
                }
                else {
                    oldPwdText.backgroundTintList  = ColorStateList.valueOf(Color.parseColor("#FC2D00"))

                    oldPwdText.clearFocus()
                    newPwdText.clearFocus()
                }
            }
        }


    }
}