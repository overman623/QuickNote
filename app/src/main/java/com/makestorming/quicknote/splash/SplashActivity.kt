package com.makestorming.quicknote.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.makestorming.quicknote.auth.AuthActivity
import com.makestorming.quicknote.database.User


class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSplashViewModel()
        checkIfUserIsAuthenticated()
    }

    private fun initSplashViewModel() {
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    private fun checkIfUserIsAuthenticated() {
        Log.d("test", "=====================checkIfUserIsAuthenticated=====================")
        splashViewModel.checkIfUserIsAuthenticated()
        splashViewModel.isUserAuthenticatedLiveData?.observe(this, Observer<User> {
            if (!it.isAuthenticated) { //인증 되지 않았을때.
                goToAuthInActivity()
                finish()
            } else { //인증 되었을때.
                getUserFromDatabase(it.uid)
            }
        })
    }

    private fun goToAuthInActivity() {
        Log.d("test", "=====================goToAuthInActivity=====================")
        val intent = Intent(this@SplashActivity, AuthActivity::class.java)
        startActivity(intent)
    }

    private fun getUserFromDatabase(uid: String) {
        Log.d("test", "=====================getUserFromDatabase=====================")
        splashViewModel.setUid(uid)
        splashViewModel.userLiveData.observe(this, Observer<User> {
            Log.d("test", "=====================Observer=====================")
            goToMainActivity(it) //it is null
            finish()
        })
    }

    private fun goToMainActivity(user: User) {
        Log.d("test", "=====================goToMainActivity=====================")
//        val intent = Intent(this@SplashActivity, MainActivity::class.java)
//        intent.putExtra(USER, user)
//        startActivity(intent)
    }

}