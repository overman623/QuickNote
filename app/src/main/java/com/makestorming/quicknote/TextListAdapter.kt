package com.makestorming.quicknote

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_text_item.view.*
import kotlin.concurrent.fixedRateTimer

class TextListAdapter(private val context: Context, items: MutableList<TextListData> ) : RecyclerView.Adapter<TextListAdapter.MainViewHolder>(){

    private var items : MutableList<TextListData>? = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainViewHolder(parent)

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_text_item, parent, false))

    override fun getItemCount(): Int = items!!.size

    interface Callback{
        fun getAction()
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        items?.get(position).let { item ->
            with(holder){
                val title = itemView.textTitle
                val date = itemView.textDate
                title.text = item?.title
                date.text = item?.date
                itemView.setOnClickListener{ v->
                    v.setBackgroundColor(Color.parseColor("#ff0000"))
                }
            }
        }
    }

}