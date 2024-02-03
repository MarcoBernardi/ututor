package com.example.ututor.models
import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable

class LessonRepo() : Serializable {
    private var lessonList = ArrayList<Lesson>()
    val logList = ArrayList<ArrayList<*>>()
    private var currentLesson = Lesson("", "", "", "", "", "", 0, 0, "", "", id = "")

    suspend fun load(university: String, professor:String?, myCallback: (result: ArrayList<Lesson>) -> Unit) {
        withContext(Dispatchers.IO) {
            val db = Firebase.firestore
            val query = if (professor != null) db.collection(university + "/lessons/group")
                .whereEqualTo(
                    "professor",
                    professor
                ) else db.collection(university + "/lessons/group")
            lessonList = ArrayList<Lesson>()
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {

                    for (d in it.result.documents) {
                        println(d.data)
                        val data = d.data?.get("data").toString()
                        val ts = d.data?.get("oraInizio").toString()
                        val scadenza = d.data?.get("scadenza").toString()

                        lessonList.add(
                            Lesson(
                                d.data?.get("nome").toString(),
                                d.data?.get("stato").toString(),
                                d.data?.get("descrizone").toString(),
                                d.data?.get("risorse").toString(),
                                scadenza.substring(
                                    scadenza.indexOf("=") + 1,
                                    scadenza.indexOf(",")
                                ),
                                data.substring(data.indexOf("=") + 1, data.indexOf(",")),
                                ts.substring(ts.indexOf("=") + 1, ts.indexOf(",")).toLong(),
                                d.data?.get("tempoTotale") as Long,
                                d.data?.get("student").toString(),
                                d.data?.get("professor").toString(),
                                d.id
                            )
                        )

                    }
                }
                myCallback.invoke(lessonList)
            }
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateLesson(university: String) {
        withContext(Dispatchers.IO){
            val db = Firebase.firestore
            lessonList[lessonList.indexOf(lessonList.find { it.getId() == currentLesson.getId() })] = currentLesson
            println(currentLesson.toHashMap())
            db.collection(university).document("lessons").collection("group")
                .document(currentLesson.getId()).update(currentLesson.toHashMap())
        }

    }

    suspend fun deleteLesson(university: String) {
        withContext(Dispatchers.IO) {
            val db = Firebase.firestore
            lessonList.removeAt(lessonList.indexOf(lessonList.find { it.getId() == currentLesson.getId() }))
            db.collection(university).document("lessons").collection("group")
                .document(currentLesson.getId()).delete()
        }
    }

    suspend fun loadLog(university: String, myCallback: (result: ArrayList<ArrayList<*>>) -> Unit) {
        withContext(Dispatchers.IO){
            val db = Firebase.firestore
            db.collection(university).document("lessons").collection("group")
                .document(currentLesson.getId()).collection("log").orderBy(
                    "data",
                    Query.Direction.ASCENDING
                ).get().addOnCompleteListener() {
                    for (d in it.result.documents) {
                        println(d.data)
                        val list: MutableList<String> = ArrayList()
                        //list.add(d.data?.get("azione").toString()) //0
                        val data = d.data?.get("data").toString()
                        list.add(data.substring(data.indexOf("=") + 1, data.indexOf(",")))//0
                        list.add(d.data?.get("student").toString()) //1
                        logList.add(list as ArrayList<*>)
                    }
                    myCallback.invoke(logList)
                }
        }

    }

    suspend fun addLog(university: String, azione: String, student :String){
        withContext(Dispatchers.IO){

        val db = Firebase.firestore
        val log = hashMapOf(
            "student" to student ,
            "azione" to azione,
            "data" to Timestamp.now()
        )
        db.collection(university).document("lessons").collection("group").document(currentLesson.getId()).collection("log").add(log)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }
    suspend fun checkState(university: String, myCallback: (result: Int) -> Unit){
        withContext(Dispatchers.IO) {

            val db = Firebase.firestore

            db.collection(university).document("lessons").collection("group")
                .whereEqualTo(FieldPath.documentId(), currentLesson.getId())
                .whereEqualTo("stato", "libero").get().addOnSuccessListener {
                val state = it.documents.size != 0
                if (!currentLesson.getRisorse().equals("None") && state)
                    myCallback.invoke(0)
                else {
                    db.collection(university).document("lessons").collection("group")
                        .whereEqualTo("stato", "in corso")
                        .whereEqualTo("risorse", currentLesson.getRisorse()).get()
                        .addOnSuccessListener { risorse ->
                            println("State: " + state + " it:" + risorse.documents.size + " Risorse: " + currentLesson.getRisorse())
                            if (state && risorse.documents.size == 0)
                                myCallback.invoke(0)
                            else if (!state && risorse.documents.size > 0)
                                myCallback.invoke(-1)
                            else if (!state)
                                myCallback.invoke(1)
                            else if (risorse.documents.size > 0)
                                myCallback.invoke(2)
                        }
                }
            }
        }
    }

    fun setCurrentLesson(work: Lesson) {
        currentLesson = work
    }

    fun getCurrentLesson(): Lesson {
        return currentLesson
    }

    fun getLessonList(): ArrayList<Lesson> {
        return lessonList
    }

    fun setLessonList(lessons: ArrayList<Lesson>)
    {
        lessonList = lessons
    }

    suspend fun addLesson(university: String, work: HashMap<String,*>){
        withContext(Dispatchers.IO) {
            val db = Firebase.firestore
            db.collection(university).document("lessons").collection("group").add(work)
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully written!"
                    )
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }

    suspend fun loadRisorse(university: String, myCallback: (result: ArrayList<String>) -> Unit){
        withContext(Dispatchers.IO) {

            val db = Firebase.firestore
            var risorseList = ArrayList<String>()
            risorseList.add("None")
            db.collection(university).document("Lavori").collection("Risorse").get()
                .addOnCompleteListener {
                    for (risorsa in it.result.documents)
                        risorseList.add(risorsa.id)
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                    myCallback.invoke(risorseList)
                }
                .addOnFailureListener { e ->

                    Log.w(ContentValues.TAG, "Error writing document", e)

                }
        }
    }

    fun complete(): Int{
        return lessonList.count { it ->
            it.getState() == "concluso"
        }
    }

    suspend fun addRisorse(university: String, value : String){
        withContext(Dispatchers.IO) {

            val db = Firebase.firestore
            db.collection(university).document("Lavori").collection("Risorse").document(value).set(
                hashMapOf("A" to "A")
            )
        }

    }
    suspend fun deleteRisorse(university: String, value : String){
        withContext(Dispatchers.IO) {

            val db = Firebase.firestore
            db.collection(university).document("Lavori").collection("Risorse").document(value)
                .delete()
        }
    }


    fun funzionePerTest(ls: ArrayList<Lesson>){
        lessonList = ls
    }
}
