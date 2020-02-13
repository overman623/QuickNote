package com.makestorming.quicknote.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.makestorming.quicknote.MainActivity
import com.makestorming.quicknote.R
import com.makestorming.quicknote.database.User


class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initSplashViewModel()
//        checkIfUserIsAuthenticated()
    }

    private fun initSplashViewModel() {
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

//        val binding: ActivitySplashBinding =
//            DataBindingUtil.setContentView(this, R.layout.activity_splash)

    }

    private fun checkIfUserIsAuthenticated(){
        splashViewModel.checkIfUserIsAuthenticated()
        splashViewModel.isUserAuthenticatedLiveData?.observe(this, Observer<User>{
            if (!it.isAuthenticated) { //인증 되지 않았을때.
                goToAuthInActivity()
                finish()
            } else { //인증 되었을때.
                getUserFromDatabase(it.uid)
            }
        })
    }

    private fun goToAuthInActivity() {
//        val intent = Intent(this@SplashActivity, AuthActivity::class.java)
//        startActivity(intent)
    }

    private fun getUserFromDatabase(uid : String) {
        splashViewModel.setUid(uid)
        splashViewModel.userLiveData?.observe(this, Observer<User>{
            goToMainActivity(it)
            finish()
        })
    }

    private fun goToMainActivity(user: User) {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
//        intent.putExtra(USER, user)
        startActivity(intent)
    }

}