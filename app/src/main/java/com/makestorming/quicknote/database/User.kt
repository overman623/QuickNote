package com.makestorming.quicknote.database

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var pass: String? = ""
)