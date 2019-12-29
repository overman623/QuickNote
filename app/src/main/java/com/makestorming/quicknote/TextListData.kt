package com.makestorming.quicknote

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TextListData(var date: Long, var title: String = "", var text: String = ""){

    constructor() : this(0, "", "")
//    constructor(firstName: String) : this(firstName, "")

}