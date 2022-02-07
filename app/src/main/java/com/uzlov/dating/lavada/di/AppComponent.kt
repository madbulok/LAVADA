package com.uzlov.dating.lavada.di

import com.uzlov.dating.lavada.di.modules.AppModule
import com.uzlov.dating.lavada.di.modules.AuthModule
import com.uzlov.dating.lavada.di.modules.FirebaseModule
import com.uzlov.dating.lavada.di.modules.ViewModelModule
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        AuthModule::class,
        FirebaseModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
}