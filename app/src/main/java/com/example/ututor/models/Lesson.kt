package com.example.ututor.models
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.collections.HashMap


class Lesson (private var name: String,
            private var state: String,
            private var descrizione : String,
            private var risorse : String,
            private var scadenza: String,
            private var data: String,
            private var oraInizio: Long,
            private var tempoTotale: Long,
            private var student: String,
              private var professor: String,
            private val id: String ) : Serializable {

    fun getName(): String { return name}
    fun setName(name: String){ this.name =  name }

    fun getState(): String { return state}
    fun setState(stateS: String) {this.state = stateS}

    fun getRisorse(): String { return risorse}
    fun setRisorse(risorse: String) { this.risorse =  risorse}

    fun getScadenza(): String { return scadenza}
    fun setScadenza(scadenza: String) { this.scadenza =  scadenza}

    fun getDescrizoine(): String { return descrizione}
    fun setDescizione(descrizione: String) { this.descrizione =  descrizione}

    fun getData(): String { return data}
    fun setData(data: String) { this.data =  data}

    fun getoraInzio(): Long { return oraInizio}
    fun setOraInizio(oraInizio: Timestamp) { this.oraInizio =  oraInizio.seconds}

    fun gettompoTotale(): Long { return tempoTotale}
    fun setTempoTotale(tempoTotale: Long) { this.tempoTotale =  tempoTotale}

    fun getStudent(): String { return student}
    fun setStudent(student: String) { this.student =  student}

    fun getProfessor(): String { return professor}
    fun setProfessor(professor: String) { this.professor =  professor}


    fun getId(): String { return id}

    @RequiresApi(Build.VERSION_CODES.O)
    fun toHashMap(): HashMap<String,*>{
        return hashMapOf(
            "nome" to getName(),
            "data" to SimpleDateFormat("dd/MM/yyyy").parse(getData()),
            "descrizone" to getDescrizoine(),
            "risorse" to getRisorse(),
            "scadenza" to SimpleDateFormat("dd/MM/yyyy").parse(getScadenza()),
            "stato" to getState(),
            "tempoTotale" to gettompoTotale(),
            "student" to getStudent(),
            "professor" to getProfessor(),
            "oraInizio" to Date(getoraInzio()*1000))

    }


}


