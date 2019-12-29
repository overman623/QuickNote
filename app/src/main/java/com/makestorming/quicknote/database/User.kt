package com.makestorming.quicknote.database

import com.google.firebase.database.IgnoreExtraProperties
import com.makestorming.quicknote.TextListData

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var uid: String? = "",
    var memo: ArrayList<TextListData>? = ArrayList()
)