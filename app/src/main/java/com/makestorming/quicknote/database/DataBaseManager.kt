package com.makestorming.quicknote.database

import androidx.databinding.ObservableArrayList
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.makestorming.quicknote.TextListData
import org.json.JSONArray

class DataBaseManager private constructor()  {
    private var email: String? = ""
    private var uid: String? = ""
    private lateinit var database: DatabaseReference// ...


    companion object {
        @Volatile private var instance: DataBaseManager? = null

        @JvmStatic fun getInstance(email : String?, uid : String?): DataBaseManager =
            instance ?: synchronized(this) {
                instance ?: DataBaseManager(email, uid).also {
                    instance = it
                }
            }
    }

    private constructor(email: String?, uid: String?) : this(){
        this.uid = uid
        this.email = email
        database = FirebaseDatabase.getInstance().reference
    }

    fun setNull(){
        instance = null
    }

    fun writeMemo(title: String, text: String, date: Long) : Boolean{
        //memo new write - //push
        //memo update - //updateChlidren
        return true
    }

    fun loadMemoTitles(): JSONArray? {//user email //user uid //return : list

        return null
    }

    fun loadMemo(): TextListData? {  //user uid //list num //return : memo item

        return null
    }

    fun renameMemo(nowTitle: String, newTitle: String, date: Long) : Boolean{
        //memo title update
        //updateChlidren
        return true
    }


    private fun writeNewUser(email: String, pass: String) {
        val user = User(email, pass)
//        database.child("users").child("1").setValue(user)
        database.child("user2").child("1").child("1").setValue(user)
    }

    private fun writeNewMemo(email: String, pass: String) {
        val user = User(email, pass)
//        database.child("users").child("1").setValue(user)
        database.child("user2").child("1").child("1").setValue(user)

    }

}