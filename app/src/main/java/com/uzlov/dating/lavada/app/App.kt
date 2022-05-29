package com.uzlov.dating.lavada.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.uzlov.dating.lavada.di.AppComponent
import com.uzlov.dating.lavada.di.DaggerAppComponent
import com.uzlov.dating.lavada.di.modules.AppModule

const val BASE_URL = "http://vm3355680.52ssd.had.wf/"

class App : Application() {
    lateinit var appComponent: AppComponent

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }