package com.example.ututor.models

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File

class UserModel {
    val db = Firebase.firestore

    fun login(ctx:Context, azienda: String, user: String, pwd: String, myCallback: (result: String) -> Unit) {
        Toast.makeText(ctx, azienda+" "+user+" "+pwd, Toast.LENGTH_SHORT).show()
        db.collection("/"+azienda+"/users/group")
            .whereEqualTo("username", user).whereEqualTo("password", pwd).get().addOnCompleteListener {
                Toast.makeText(ctx, "CIAO-"+it.result.size().toString(), Toast.LENGTH_LONG).show()
                Log.d(TAG, "VAFFANCULO")

                if (it.result.size() == 1) {
                    Toast.makeText(ctx, "CIAO2", Toast.LENGTH_LONG).show()
                    myCallback(it.result.documents[0].data?.getValue("role").toString())
                }
                else
                    myCallback("error")

            }
    }

    fun updatePwd(azienda: String, user: String, pwd: String, newPwd: String ,myCallback: (result: Boolean) -> Unit) {
        db.collection("/" + azienda + "/users/group")
            .whereEqualTo("user", user)
            .whereEqualTo("password", pwd).get().addOnCompleteListener {
                if (it.result.size() == 1) {
                    db.collection("/" + azienda + "/users/group").document(it.result.documents[0].id).update("users", newPwd)
                    myCallback(true)
                }
                else
                    myCallback(false)

            }
    }


}