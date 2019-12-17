package com.makestorming.quicknote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MemoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        intent.getStringExtra("DATE")
        intent.getStringExtra("TITLE")
        intent.getIntExtra("ORDER", 0)

    }


}
