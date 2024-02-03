package com.example.ututor.viewadapters
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ututor.R

class LoggerViewAdapter (private val context: Activity, private val dateI: MutableList<String>, private val dateF: MutableList<String>, private val User: MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.logview_row,dateI) {
//private val imgid: Array<Int> va nella classe

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.logview_row, null, true)


        val dateStart = rowView.findViewById(R.id.dateI) as TextView
        val dateDone = rowView.findViewById(R.id.dateF) as TextView
        val username = rowView.findViewById(R.id.user) as TextView
        // val stateText = rowView.findViewById(R.id.state) as TextView

        dateStart.text = "Inizio: " + dateI[position]
        if(position == dateF.size)  dateDone.text = "Fine: In Corso"
        if(position < dateF.size) dateDone.text = "Fine: " + dateF[position]
        username.text = User[position]

        // stateText.text = state[position]
        return rowView
    }

}