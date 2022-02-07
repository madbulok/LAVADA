package com.uzlov.dating.lavada.di.modules

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.uzlov.dating.lavada.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Singleton
    @Provides
    fun provideAuth() : FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideApp(app: App) : FirebaseApp? = Firebase.initialize(app)

    @Singleton
    @Provides
    fun provideRealtimeDatabase() : FirebaseDatabase = Firebase.database
}