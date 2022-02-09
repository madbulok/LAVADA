package com.uzlov.dating.lavada.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


data class User(val name: String, val phone: String, val password: String)

class FirebasePhoneAuthService @Inject constructor(private val auth: FirebaseAuth) :
    IAuth<User, Activity> {


  fun startAuth(user: User, host: Activity) {
        auth.createUserWithEmailAndPassword(user.name, user.password)
            .addOnCompleteListener(host) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.

                }
            }.addOnFailureListener {

            }
    }

    override fun logout() {
        auth.signOut()
    }

     fun registered(login: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun login(login: String, password: String) {
        TODO("Not yet implemented")
    }

}


