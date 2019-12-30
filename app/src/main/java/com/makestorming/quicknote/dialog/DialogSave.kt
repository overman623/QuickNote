package com.makestorming.quicknote.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.makestorming.quicknote.R
import kotlinx.android.synthetic.main.dialog_save.*

class DialogSave(context: Context, private val title : String, private val isExit : Boolean, private val callback : Callback) : AlertDialog(context) {

    val TAG = DialogSave::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {

        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//        lpWindow.dimAmount = 1.0f;
        lpWindow.dimAmount = 0.8f
//        getWindow().setAttributes(lpWindow);
//        getWindow().setLayout((int)(displayX * 0.9), (int)(displayY * 0.5));
        window!!.setGravity(Gravity.CENTER)
//        window!!.(R.style.MyAnimation_Window)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_save)
        setLayout()
    }

    private fun setLayout() {
//        testCallback {
//            Log.d("Test","Called : "+it)
//        }

        editTitle.setText(title)
        buttonSave.setOnClickListener{
            editTitle.text.toString().let {
                val matched = Regex(pattern = "[:\\\\/%*?:|\"<>]").containsMatchIn(input = it)
                if(matched) textAlert.setText(R.string.dialog_no_char)
                else if (it.isEmpty() || it.isBlank()) textAlert.setText(R.string.dialog_text_blank)
                else {
                    dismiss()
                    callback.getTitle(title, it)
                }
            }
        }

        if(isExit) buttonExit.visibility = View.VISIBLE
        buttonExit.setOnClickListener {
            dismiss()
            callback.exit()
        }

    }

    interface Callback {
        fun getTitle(nowTitle : String, newTitle: String?)
        fun exit()
    }

    fun testCallback(callback: ((String)->Unit)) {
        callback.invoke("test")
    }

}