package com.makestorming.quicknote

import android.graphics.Color
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_text_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TextListAdapter(items: MutableList<TextListData>, private val connector: Callback ) : RecyclerView.Adapter<TextListAdapter.MainViewHolder>(){

    private var items : MutableList<TextListData>? = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainViewHolder(parent)

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_text_item, parent, false))

    override fun getItemCount(): Int = items!!.size

    interface Callback{
        fun getAction(item : TextListData?)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        items?.get(position).let { item ->
            with(holder){
//                val title = itemView.textTitle
//                val date = itemView.textDate
                itemView.textTitle.text = item?.title
                itemView.textDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(item?.date)
                itemView.textContent.text = item?.text
                itemView.setOnClickListener{
                    connector.getAction(item)
                }
            }
        }
    }

}