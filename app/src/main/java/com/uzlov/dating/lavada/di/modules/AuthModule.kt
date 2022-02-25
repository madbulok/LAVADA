package com.uzlov.dating.lavada.di.modules

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.auth.*
import com.uzlov.dating.lavada.domain.models.User
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {
    @Singleton
    @Provides
    fun providePhoneAuthService(auth: FirebaseAuth): IAuth<User, Activity> =
        FirebasePhoneAuthService(auth)

    @Singleton
    @Provides
    fun provideEmailAuthService(auth: FirebaseAuth): IAuth<User, Activity> =
        FirebaseEmailAuthService(auth)

    @Singleton
    @Provides
    fun provideGoogleSigInAuthService(
        auth: FirebaseAuth,
        app: App,
    ): IAuth<GoogleSignInAccount, Activity> = FirebaseGoogleSignInAuthService(auth, app)
}