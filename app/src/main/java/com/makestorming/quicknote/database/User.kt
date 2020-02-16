package com.makestorming.quicknote.database

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.makestorming.quicknote.MemoListData
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var uid: String? = "",
    var memo: ArrayList<MemoListData>? = ArrayList()
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "uid" to uid,
            "memo" to memo
        )
    }
}