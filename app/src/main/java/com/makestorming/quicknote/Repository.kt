package com.makestorming.quicknote

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.makestorming.quicknote.database.User


class Repository(application: Application) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential?): MutableLiveData<User>? {
        val authenticatedUserMutableLiveData =
            MutableLiveData<User>()
        firebaseAuth.signInWithCredential(googleAuthCredential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = null
//                            User(uid, name, email)
//                        user.isNew = isNewUser
                        authenticatedUserMutableLiveData.setValue(user)
                    }
                } else {
//                    logErrorMessage(authTask.exception!!.message)
                }
            }
        return authenticatedUserMutableLiveData
    }


}