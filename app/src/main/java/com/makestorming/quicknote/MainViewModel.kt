package com.makestorming.quicknote

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var userKey : ObservableField<String> = ObservableField()
    var list : ObservableArrayList<MemoListData> = ObservableArrayList()
    var position : ObservableInt = ObservableInt()
    var email : ObservableField<String> = ObservableField()
    var uid : ObservableField<String> = ObservableField()
    var verified : ObservableBoolean = ObservableBoolean()
    var listNum : ObservableInt = ObservableInt()

}
