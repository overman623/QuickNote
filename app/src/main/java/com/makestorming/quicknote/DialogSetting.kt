package com.makestorming.quicknote

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

class DialogSetting(context: Context) : AlertDialog(context) {

    val TAG = DialogSetting::class.java.name

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
//        setContentView(R.layout.layout_alert_setting)
        setLayout()
    }

    private fun setLayout() {

    }




}
