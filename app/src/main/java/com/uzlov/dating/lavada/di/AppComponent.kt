package com.uzlov.dating.lavada.di

import com.uzlov.dating.lavada.di.modules.*
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import com.uzlov.dating.lavada.ui.fragments.profile.AboutMyselfFragment
import com.uzlov.dating.lavada.ui.fragments.profile.UploadVideoFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        AuthModule::class,
        FirebaseModule::class,
        ViewModelModule::class,
        BillingModule::class,
        MediaModule::class,
        StorageModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(aboutMyselfFragment: AboutMyselfFragment)
    fun inject(mainVideosFragment: MainVideosFragment)
    fun inject(uploadVideoFragment: UploadVideoFragment)
}