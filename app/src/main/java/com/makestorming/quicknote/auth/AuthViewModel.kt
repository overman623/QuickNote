package com.makestorming.quicknote.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.AuthCredential
import com.makestorming.quicknote.database.User


class AuthViewModel(application: Application) : AndroidViewModel(Application()){

    private var authRepository: AuthRepository = AuthRepository()
    var authenticatedUserLiveData: LiveData<User>? = null
    var createdUserLiveData: LiveData<User>? = null

    fun signInWithGoogle(googleAuthCredential: AuthCredential?) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun createUser(authenticatedUser: User?) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser)
    }

}