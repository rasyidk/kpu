package com.example.kpu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kpu.R

class MenuAdapter(context: Context, private val items: List<String>,  private val icons: List<Int>) :
    ArrayAdapter<String>(context, R.layout.list_menu, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_menu, parent, false)
        val text = view.findViewById<TextView>(R.id.item_text)
        val icon = view.findViewById<ImageView>(R.id.item_icon)

        text.text = items[position]
        icon.setImageResource(icons[position])


        return view
    }
}
