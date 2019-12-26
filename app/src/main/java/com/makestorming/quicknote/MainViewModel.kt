package com.makestorming.quicknote

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableInt

class MainViewModel : ViewModel() {

//    val textData : MutableList<TextListData> = mutableListOf()

    var list : ObservableArrayList<TextListData> = ObservableArrayList()
    var index : ObservableInt = ObservableInt()

}
