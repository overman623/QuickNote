package com.makestorming.quicknote.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.makestorming.quicknote.database.User


class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential?): LiveData<User>? {
        val authenticatedUserMutableLiveData : MutableLiveData<User> = MutableLiveData()
        return authenticatedUserMutableLiveData
    }

    fun createUserInFirestoreIfNotExists(authenticatedUser: User?): LiveData<User>? {
        val authenticatedUserMutableLiveData : MutableLiveData<User> = MutableLiveData()
        return authenticatedUserMutableLiveData

    }

}