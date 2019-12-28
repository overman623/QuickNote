package com.makestorming.quicknote

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableInt

class MainViewModel : ViewModel() {

    var list : ObservableArrayList<TextListData> = ObservableArrayList()
    var index : ObservableInt = ObservableInt()
    var email : ObservableField<String> = ObservableField()
    var uid : ObservableField<String> = ObservableField()
    var verified : ObservableBoolean = ObservableBoolean()

}
