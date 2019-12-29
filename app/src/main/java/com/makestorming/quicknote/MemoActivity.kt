package com.makestorming.quicknote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.makestorming.quicknote.config.FileManager
import com.makestorming.quicknote.dialog.DialogFontSize
import com.makestorming.quicknote.dialog.DialogSave
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*
import java.io.File

/*

LiveData를 공유하는 무언가가 있었으면 좋을것 같음.

*/

class MemoActivity : AppCompatActivity() {

    val tag = MemoActivity::class.java.name

    private var date : String? = null
    private var nowTitle : String? = ""
    private var text : String? = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent.apply {
            date = getStringExtra("DATE")
            nowTitle = getStringExtra("TITLE")?.let {
                text = getStringExtra("TEXT")
                it
            } ?: ""
        }

        title = if(nowTitle == "") getString(R.string.text_new_file)
                else nowTitle

        fab.setOnClickListener {
            showDialogSave(false) //toast
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
            super.onBackPressed()
        }else{
            showDialogSave(true)
        }
    }

    private fun showDialogSave(isExit : Boolean){
        DialogSave(
            this,
            nowTitle!!,
            isExit,
            object : DialogSave.Callback {
                override fun getTitle(nowTitle : String, newTitle: String?) {
                    /*if (saveText(text, title)) { //true : activity close
                        setResult(PICK_CONTACT_REQUEST)
                        this@MemoActivity.finish()
                    }*/

                    //그전에 등록했던 파일이 존재하는지 여부를 파악해야함.
                    //saveText에서 리턴한 파일은제외함.

                    saveText(newTitle, nowTitle).let {
                        val keyWord = if(nowTitle.isEmpty()){
                            FILE_WRITE
                        }else{
                            FILE_RENAME
                        }

                        setResult(keyWord, Intent().apply {
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

    private fun saveText(newTitle: String?, beforeTitle: String?) : File {
        FileManager(newTitle!!, beforeTitle!!).apply {
            return makeFile(date, editText.text.toString())
        }
    }

}
