package com.example.ututor.viewadapters
import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ututor.R

class ResourcesViewAdapter (private val context: Activity, private val resources :MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.resouceview_row,resources) {

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.resouceview_row, null, true)
        val resourceTextView = rowView.findViewById(R.id.resource) as TextView
        /*if(state.contains(resources [position])) {
            resourceTextView.setTextColor(R.color.red)
        }*/
        resourceTextView.text = "Risorsa: " + resources [position]

        return rowView
    }
}