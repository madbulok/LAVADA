package com.uzlov.dating.lavada.di

import com.uzlov.dating.lavada.ui.fragments.profile.ProfileFragment
import com.uzlov.dating.lavada.di.modules.*
import com.uzlov.dating.lavada.ui.activities.SplashActivity
import com.uzlov.dating.lavada.ui.fragments.ChatsFragment
import com.uzlov.dating.lavada.ui.fragments.FilterSearchPeopleFragment
import com.uzlov.dating.lavada.ui.fragments.FragmentOpenChat
import com.uzlov.dating.lavada.ui.fragments.MainVideosFragment
import com.uzlov.dating.lavada.ui.fragments.profile.*
import com.uzlov.dating.lavada.ui.fragments.registration.LogInFragment
import com.uzlov.dating.lavada.ui.fragments.registration.RegistrationFragment
import com.uzlov.dating.lavada.ui.fragments.settings.SettingsFragment
import com.uzlov.dating.lavada.ui.fragments.settings.UpdatePasswordFragment
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        AuthModule::class,
        FirebaseModule::class,
        ViewModelModule::class,
        BillingModule::class,
        StorageModule::class,
        GeoModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(aboutMyselfFragment: AboutMyselfFragment)
    fun inject(mainVideosFragment: MainVideosFragment)
    fun inject(uploadVideoFragment: UploadVideoFragment)
    fun inject(logInFragment: LogInFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(chatsTelegram: ChatsFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(updatePasswordFragment: UpdatePasswordFragment)
    fun inject(fragmentOpenChat: FragmentOpenChat)
    fun inject(splashActivity: SplashActivity)
    fun inject(filterLookingForFragment: FilterLookingForFragment)
    fun inject(previewVideoFragment: PreviewVideoFragment)
    fun inject(filterSearchPeopleFragment: FilterSearchPeopleFragment)
}