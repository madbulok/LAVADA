package com.uzlov.dating.lavada.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.auth.FirebaseGoogleSignInAuthService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {

    @Singleton
    @Provides
    fun provideEmailAuthService(auth: FirebaseAuth): FirebaseEmailAuthService =
        FirebaseEmailAuthService(auth)

    @Singleton
    @Provides
    fun provideGoogleSigInAuthService(
        auth: FirebaseAuth,
        app: App,
    ): FirebaseGoogleSignInAuthService = FirebaseGoogleSignInAuthService(auth, app)
}