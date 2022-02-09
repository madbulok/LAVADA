package com.uzlov.dating.lavada.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.uzlov.dating.lavada.R
import javax.inject.Inject


class FirebaseEmailAuthService @Inject constructor(val auth: FirebaseAuth) : IAuth<User, Activity> {

    override fun login(T: String, A: String) {
        auth.signInWithEmailAndPassword(T, A)
            .addOnCompleteListener(Activity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
    }

    override fun logout() {
        auth.signOut()
    }

    fun registered(login: String, password: String) {
        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener(Activity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    fun getGSO(context: Context): GoogleSignInOptions {
        return  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(Activity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithGoogle:success")
                } else {
                    Log.w(TAG, "createUserWithGoogle:failure", task.exception)
                }
            }
    }
    companion object {
        const val TAG = "EmailPassword"
    }
}


