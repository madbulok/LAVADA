package com.uzlov.dating.lavada.auth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.uzlov.dating.lavada.app.App
import javax.inject.Inject


class FirebaseGoogleSignInAuthService @Inject constructor(private val auth: FirebaseAuth, private val app: App) : IAuth<GoogleSignInAccount, Activity> {

    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var account: GoogleSignInAccount? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    init {
        createAuthConnection()
        account = GoogleSignIn.getLastSignedInAccount(app)
        if (account != null) {
//            loginSuccess(account!!)
        } else {
//            loginFailed(Exception("Failed log in Google account!"))
        }
    }


    private fun createAuthConnection() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(app, gso)
    }

    fun handleSignInResult(data: Intent?) {
        try {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener {
//                    loginSuccess(it)
                }.addOnFailureListener {
//                    loginFailed(it)
                }
        } catch (e: ApiException) {
//            loginFailed(e)
        }
    }

    fun startAuth(user: GoogleSignInAccount, host: Activity) {
        if (account == null) {
            mGoogleSignInClient?.signInIntent?.let { signInIntent ->
//                startLogin(signInIntent)
            }
        } else {
//            loginSuccess(account!!)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun login(login: String, password: String) {
        TODO("Not yet implemented")
    }

}


