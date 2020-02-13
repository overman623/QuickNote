package com.makestorming.quicknote.splash

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.makestorming.quicknote.database.User

@SuppressWarnings("ConstantConditions")
class SplashRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val user : User = User("", "", null, false) //빈 생성자가 되어야함.
    private val rootRef  = FirebaseDatabase.getInstance()
    private val usersRef = rootRef.reference

    fun checkIfUserIsAuthenticatedInFirebase() : MutableLiveData<User>  {
        val isUserAuthenticateInFirebaseMutableLiveData: MutableLiveData<User> = MutableLiveData()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            user.isAuthenticated = false
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user)
        } else {
            user.uid = firebaseUser.uid
            user.isAuthenticated = true
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user)
        }
        return isUserAuthenticateInFirebaseMutableLiveData
    }

    fun addUserToLiveData(uid : String) : MutableLiveData<User> { //추가된 유저의 정보가 반환되어야 할것 같음.
        val userMutableLiveData: MutableLiveData<User> = MutableLiveData()
/*
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult()
                if(document.exists()) {
                    User user = document.toObject(User.class)
                    userMutableLiveData.setValue(user)
                }
            } else {
//                logErrorMessage(userTask.getException().getMessage())
            }
        })*/
        return userMutableLiveData
    }
}