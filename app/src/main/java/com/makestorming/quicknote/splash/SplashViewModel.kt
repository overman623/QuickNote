package com.makestorming.quicknote.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.makestorming.quicknote.database.User


class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private var splashRepository: SplashRepository = SplashRepository()
//    var isUserAuthenticatedLiveData: MutableLiveData<User>? = null
    var isUserAuthenticatedLiveData: MutableLiveData<User> = MutableLiveData()
    var userLiveData: MutableLiveData<User> = MutableLiveData()

    fun checkIfUserIsAuthenticated() {
        Log.d("test" , "=====================checkIfUserIsAuthenticated model=====================")
        isUserAuthenticatedLiveData.value = splashRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun setUid(uid: String) {
        userLiveData.value = splashRepository.addUserToLiveData(uid)
    }
}
