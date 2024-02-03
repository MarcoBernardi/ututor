package com.example.ututor.models

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo {
    val db = Firebase.firestore

    suspend fun login( university: String, user: String, pwd: String, myCallback: (result: String) -> Unit) {
        withContext(Dispatchers.IO) {
            db.collection("/"+university+"/users/group")
                .whereEqualTo("username", user).whereEqualTo("password", pwd).get().addOnCompleteListener {
                    if (it.result.size() == 1) {

                        myCallback(it.result.documents[0].data?.getValue("role").toString())
                    }
                    else
                        myCallback("error")

                }
        }

    }

    fun updatePwd(ctx:Context, university: String, user: String, pwd: String, newPwd: String ,myCallback: (result: Boolean) -> Unit) {
        db.collection("/" + university + "/users/group")
            .whereEqualTo("username", user)
            .whereEqualTo("password", pwd).get().addOnCompleteListener {
                if (it.result.size() == 1) {
                    db.collection("/" + university + "/users/group").document(it.result.documents[0].id).update("password", newPwd)
                    myCallback(true)
                }
                else
                    myCallback(false)

            }
    }


}