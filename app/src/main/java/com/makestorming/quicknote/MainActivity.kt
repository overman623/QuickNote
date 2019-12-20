package com.makestorming.quicknote

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            openWriteActivity(null)
        }

        val textItems1 : MutableList<TextListData> = mutableListOf()
        File(Environment.getDownloadCacheDirectory().absolutePath + File.separator + "memo")
            .apply {
                if(exists()){
                    listFiles()?.forEach {
                        textItems1.add(0, TextListData(0, "date", it.name))
                        //date
                        //order
                    }
                }else{
                    mkdirs()
                }
            }

        val textItems : MutableList<TextListData> = mutableListOf(TextListData(0,"date1", "Title1", "Text1"),
            TextListData(0,"date2", "Title2", "Text2"),TextListData(0,"date3", "Title3", "Text3"))

        if(textItems.size == 0) textCenter.text = getString(R.string.text_center)

        val mAdapter = TextListAdapter(textItems, object : TextListAdapter.Callback{
            override fun getAction(item : TextListData?) {
                openWriteActivity(item)
            }
        })

        textViewList.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        textViewList.layoutManager = lm
        textViewList.setHasFixedSize(true)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openWriteActivity(item : TextListData?) {
        val intent = Intent(this, MemoActivity::class.java)
        startActivity(intent.apply {
            item?.let {
                putExtra("TITLE", it.title)
                putExtra("DATE", it.date)
                putExtra("TEXT", it.text)
                putExtra("ORDER", it.order)
            }
        })
    }


}
