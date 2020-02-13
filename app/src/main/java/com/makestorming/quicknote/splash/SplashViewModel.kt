package com.makestorming.quicknote.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.makestorming.quicknote.database.User


class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private var splashRepository: SplashRepository = SplashRepository()
    var isUserAuthenticatedLiveData: LiveData<User>? = null
    var userLiveData: LiveData<User>? = null

    fun checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = splashRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun setUid(uid: String) {
        userLiveData = splashRepository.addUserToLiveData(uid)
    }
}
