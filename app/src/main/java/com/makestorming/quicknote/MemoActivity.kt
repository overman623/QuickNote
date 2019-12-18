package com.makestorming.quicknote

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*


class MemoActivity : AppCompatActivity() {

    private var date : String? = null
    private var title : String? = ""
    private var text : String? = ""
    private var order : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent.apply {
            date = getStringExtra("DATE")
            title = getStringExtra("TITLE")
            text = getStringExtra("TEXT")
            order = getIntExtra("ORDER", 0)
        }

        text = if(text == null) "" else text

        fab.setOnClickListener {
            saveText()
            //toast
        }

        editText.setText(text)
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
                editText.setText("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onBackPressed() {

        Log.d("MemoActivity", editText.text.toString())

/*
        val data = when(editText.text.toString()){
            text -> true
            else -> false
        }
*/

//        editText.text.toString().equals(text)

        if(editText.text.toString() == text){
            Log.d("MemoActivity", "if")
            //show dialog : would you save the text
            super.onBackPressed()
        }else{
            Log.d("MemoActivity", "else")
            //dialog save text
        }

    }

    private fun saveText(){
        //use title, date, order
//        Environment.getExternalStorageDirectory().getAbsolutePath()
        Log.d("MemoActivity", Environment.getDataDirectory().absolutePath)
//        val file : File = File.createTempFile()
    }


}
