package com.makestorming.quicknote

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TextListData(var key: String="", var date: Long, var title: String = "", var text: String = ""){

//    constructor() : this( "", 0, "", "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "date" to date,
            "title" to title,
            "text" to text
        )
    }

}