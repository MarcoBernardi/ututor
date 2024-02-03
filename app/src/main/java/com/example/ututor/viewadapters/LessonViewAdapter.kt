package com.example.ututor.viewadapters
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.ututor.R

class LessonViewAdapter(private val context: Activity, private val user: MutableList<String>,private val title: MutableList<String>, private val state: MutableList<String>, private val id: MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.lessonview_row, id) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.lessonview_row, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.iconC) as ImageView
        val idLessonText = rowView.findViewById(R.id.user) as TextView
        val images = arrayOf(R.drawable.bluestate,R.drawable.yellowstate,R.drawable.greenstate)

        titleText.text = title[position]
        if(!user[position].isEmpty())
            idLessonText.text = "done by: " + user[position]
        else
            idLessonText.text = "ID lesson: " + id[position]

        if(state[position]=="libero"){
            imageView.setImageResource(images[0])
        }
        if(state[position]=="in corso"){
            imageView.setImageResource(images[1])
        }
        if(state[position]=="concluso"){
            imageView.setImageResource(images[2])
        }
        return rowView
    }

}