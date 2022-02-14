package com.uzlov.dating.lavada.di

import com.uzlov.dating.lavada.di.modules.*
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        AuthModule::class,
        FirebaseModule::class,
        ViewModelModule::class,
        BillingModule::class,
        StorageModule::class
    ]
)
interface AppComponent {
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(aboutMyselfFragment: AboutMyselfFragment)
    fun inject(uploadVideoFragment: UploadVideoFragment)
}