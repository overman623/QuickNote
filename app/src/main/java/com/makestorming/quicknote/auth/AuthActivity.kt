package com.makestorming.quicknote.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.makestorming.quicknote.MainActivity
import com.makestorming.quicknote.R
import com.makestorming.quicknote.database.User
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity  : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initSignInButton()
        initAuthViewModel()
        initGoogleSignInClient()
    }

    private fun initSignInButton() {
        googleSignInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun initAuthViewModel() {
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    getGoogleAuthCredential(account)
                }
            } catch (e: ApiException) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential =
            GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential)
        authViewModel.authenticatedUserLiveData!!.observe(this,
            Observer { authenticatedUser: User ->
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser)
                } else {
                    goToMainActivity(authenticatedUser)
                }
            }
        )
    }

    private fun createNewUser(authenticatedUser: User) {
        authViewModel.createUser(authenticatedUser)
        authViewModel.createdUserLiveData!!.observe(
            this,
            Observer { user: User ->
                if (user.isCreated) {
                    toastMessage(user.uid)
                }
                goToMainActivity(user)
            }
        )
    }

    private fun toastMessage(name: String) {
        Toast.makeText(
            this,
            "Hi $name!\nYour account was successfully created.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun goToMainActivity(user: User) {
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        intent.putExtra(USER, user)
        startActivity(intent)
        finish()
    }

    companion object{
        const val RC_SIGN_IN = 100
        const val USER = "user"
    }

}