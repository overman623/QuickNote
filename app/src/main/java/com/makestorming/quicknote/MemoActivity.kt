package com.makestorming.quicknote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.makestorming.quicknote.dialog.DialogFontSize
import com.makestorming.quicknote.dialog.DialogSave
import kotlinx.android.synthetic.main.activity_memo.*
import kotlinx.android.synthetic.main.content_memo.*

/*

LiveData를 공유하는 무언가가 있었으면 좋을것 같음.

*/

class MemoActivity : AppCompatActivity() {

    val tag = MemoActivity::class.java.name

    private var memoKey : String? = ""
    private var nowTitle : String? = ""
    private var text : String? = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent.apply {
            memoKey = getStringExtra("KEY")
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
            Log.d(tag, "equals")
            super.onBackPressed()
        }else{
            Log.d(tag, "not equals")
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
                    val keyWord = if(nowTitle.isEmpty()){
                        MEMO_WRITE
                    }else{
                        MEMO_RENAME
                    }
                    setResult(keyWord, saveMemo(newTitle, nowTitle))
                    this@MemoActivity.finish()
                }

                override fun exit() {
                    this@MemoActivity.finish()
                }
            }).apply {
            show()
        }
    }

    private fun saveMemo(newTitle: String?, beforeTitle: String?) : Intent{
        return Intent().apply {
            putExtra("KEY", memoKey)
            putExtra("TITLE_BEFORE", beforeTitle)
            putExtra("TITLE_NEW", newTitle)
            putExtra("TEXT", editText.text.toString())
        }
    }

}
