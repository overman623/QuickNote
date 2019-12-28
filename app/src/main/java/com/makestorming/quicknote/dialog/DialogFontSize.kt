package com.makestorming.quicknote.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.makestorming.quicknote.R
import kotlinx.android.synthetic.main.dialog_font_size.*

class DialogFontSize(context: Context, private val callback : Callback) : AlertDialog(context) {

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
        setContentView(R.layout.dialog_font_size)
        setLayout()
    }

    private fun setLayout() {

        radioGroup.check(R.id.radioButton2)
        buttonSignIn.setOnClickListener {
            val size = when(radioGroup.checkedRadioButtonId){
                R.id.radioButton1 -> 14
                R.id.radioButton2 -> 18
                R.id.radioButton3 -> 28
                else -> 0
            }
            callback.getSize(changeSizeInt(size))
            cancel()
        }

    }

    private fun changeSizeInt(size : Int) : Float{
        return size.toFloat()
    }

    interface Callback{
        fun getSize(size : Float)
    }

}
