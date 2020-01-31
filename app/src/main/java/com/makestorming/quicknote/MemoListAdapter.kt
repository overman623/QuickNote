package com.makestorming.quicknote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_text_item.view.*

class MemoListAdapter(items: MutableList<MemoListData>, private val connector: Callback ) : RecyclerView.Adapter<MainViewHolder>(){

    private val tag = MemoListAdapter::class.java.simpleName
    private var items : MutableList<MemoListData>? = items
    val setData : MutableSet<MemoListData> = mutableSetOf()
    var deleteMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainViewHolder(parent)

    override fun getItemCount(): Int = items!!.size

    interface Callback{
        fun getAction(item : MemoListData?, index : Int)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        items?.get(position).let { item ->
            with(holder){
                itemView.textTitle.text = item?.title
                itemView.textDate.text = java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
                ).format(item?.date)
                itemView.textContent.text = item?.text
                itemView.imageCheck.visibility = android.view.View.GONE
                itemView.setOnClickListener{
                    if(deleteMode){
                        if(it.imageCheck.visibility == android.view.View.GONE){
                            it.imageCheck.visibility = android.view.View.VISIBLE
                            setData.add(item!!)
                        }else{
                            it.imageCheck.visibility = android.view.View.GONE
                            setData.remove(item)
                        }
                    }else{
                        connector.getAction(item, position)
                    }

                }
            }
        }
    }
}

class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.layout_text_item, parent, false))