package com.makestorming.quicknote

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableInt

class MainViewModel : ViewModel() {

    var userKey : ObservableField<String> = ObservableField()
    var list : ObservableArrayList<MemoListData> = ObservableArrayList()
    var position : ObservableInt = ObservableInt()
    var email : ObservableField<String> = ObservableField()
    var uid : ObservableField<String> = ObservableField()
    var verified : ObservableBoolean = ObservableBoolean()
    var listNum : ObservableInt = ObservableInt()

}
