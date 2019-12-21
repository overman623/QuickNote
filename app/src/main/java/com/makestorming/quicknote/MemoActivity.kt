package com.makestorming.quicknote

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*
import java.io.File


class MemoActivity : AppCompatActivity() {

    val TAG = MemoActivity::class.java.name

    private var date : String? = null
    private var title : String? = ""
    private var text : String? = ""
    private var order : Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent.apply {
            date = getStringExtra("DATE")
            title = getStringExtra("TITLE")?.let {
                text = FileManager(it).readFile() //readfile
                it
            } ?: ""
            order = getIntExtra("ORDER", 0)
//            text = getStringExtra("TEXT")?.let { it } ?: ""
        }
        setTitle(title)

        fab.setOnClickListener {
            showDialogSave(false)
            //toast
        }

        //title write
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
        if(editText.text.toString() == text){
            Log.d("MemoActivity", "if")
            //show dialog : would you save the text
            super.onBackPressed()
        }else{
            showDialogSave(true)
            Log.d("MemoActivity", "else")
            //dialog save text
        }
    }

    private fun showDialogSave(isExit : Boolean){
        DialogSave(this, title!!, isExit, object : DialogSave.Callback{
            override fun getTitle(text: String?) {
                if(saveText(text, title)) { //true : activity close
                    this@MemoActivity.finish()
                }
            }
            override fun exit() {
                this@MemoActivity.finish()
            }
        }).apply {
            show()
        }
    }

    private fun saveText(title: String?, beforeTitle: String?) : Boolean{
        Log.d(TAG, editText.text.toString())
        FileManager(title!!, beforeTitle!!).apply {
            return makeFile(date, editText.text.toString(), order)
        }

    }


}
