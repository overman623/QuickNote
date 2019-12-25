package com.makestorming.quicknote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.makestorming.quicknote.config.FileManager
import com.makestorming.quicknote.dialog.DialogFontSize
import com.makestorming.quicknote.dialog.DialogSave
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*
import java.io.File


class MemoActivity : AppCompatActivity() {

    val tag = MemoActivity::class.java.name

    private val PICK_CONTACT_REQUEST = 1

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
        }

        setTitle(
            if(title == "") getString(R.string.text_new_file)
            else title
        )

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
            R.id.action_save-> {
                showDialogSave(false)
                true
            }
            R.id.action_font_size -> {
                DialogFontSize(
                    this,
                    object :
                        DialogFontSize.Callback {
                        override fun getSize(size: Float) {
                            editText.textSize = size
                        }
                    }).show()
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
            setResult(PICK_CONTACT_REQUEST)
            super.onBackPressed()
        }else{
            showDialogSave(true)
        }
    }

    private fun showDialogSave(isExit : Boolean){
        DialogSave(
            this,
            title!!,
            isExit,
            object : DialogSave.Callback {
                override fun getTitle(text: String?) {
                    /*if (saveText(text, title)) { //true : activity close
                        setResult(PICK_CONTACT_REQUEST)
                        this@MemoActivity.finish()
                    }*/

                    saveText(text, title).let {
                        setResult(PICK_CONTACT_REQUEST, Intent().apply {
                            putExtra("FILE_MAKE_DATE", it.lastModified())
                            putExtra("FILE_NAME", it.name.replace(".txt", ""))
                            putExtra("FILE_READ_LINE", FileManager().readLine(it))
                        })
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

    private fun saveText(title: String?, beforeTitle: String?) : File {
        FileManager(title!!, beforeTitle!!).apply {
            return makeFile(date, editText.text.toString(), order)
        }
    }

}
