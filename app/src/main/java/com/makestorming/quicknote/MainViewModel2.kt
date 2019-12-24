package com.makestorming.quicknote;

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel


class MainViewModel2 : ViewModel() {

    val textData : MutableList<TextListData> = mutableListOf()
    val centerText = ObservableField("")

    init{
        centerText.set("test")
    }




}
