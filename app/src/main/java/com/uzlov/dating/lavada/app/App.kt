package com.uzlov.dating.lavada.app

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.uzlov.dating.lavada.di.AppComponent
import com.uzlov.dating.lavada.di.DaggerAppComponent
import com.uzlov.dating.lavada.di.modules.AppModule
import com.uzlov.dating.lavada.ui.adapters.toast

class App : Application() {
    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseAuth.getInstance()
            .currentUser?.getIdToken(false)?.addOnSuccessListener {
            if (it.token.isNullOrEmpty()){
                toast("Token error!")
            } else {
                tokenUser = it.token.toString()
            }
        }?.addOnFailureListener {
            toast("Token error! ${it.message}")
        }
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        private lateinit var tokenUser: String
        fun getToken() : String = tokenUser
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }