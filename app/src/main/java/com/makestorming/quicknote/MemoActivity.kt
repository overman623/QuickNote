package com.makestorming.quicknote

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_memo.*


class MemoActivity : AppCompatActivity() {

    private var date : String? = null
    private var title : String? = null
    private var order : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        date = intent.getStringExtra("DATE")
        title = intent.getStringExtra("TITLE")
        order = intent.getIntExtra("ORDER", 0)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_memo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                //setting dialog
                true
            }
            R.id.action_font_size -> {
                //font size dialog
                true
            }
            R.id.action_erase_all -> {
                //erase field
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    override fun onBackPressed() {
        var before = editText.text.toString()
        if(before == title) //show dialog : would you save the text
        super.onBackPressed()
    }


}
