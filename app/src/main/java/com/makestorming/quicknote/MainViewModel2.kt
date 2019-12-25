package com.makestorming.quicknote;

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField

class MainViewModel2 : ViewModel() {

//    val textData : MutableList<TextListData> = mutableListOf()

    var list : ObservableArrayList<TextListData> = ObservableArrayList()

    init{
    }


}
