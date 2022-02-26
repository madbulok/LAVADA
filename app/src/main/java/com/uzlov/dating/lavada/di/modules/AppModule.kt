package com.uzlov.dating.lavada.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.repository.PreferenceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Provides
    fun app(): Context = app

    @Singleton
    @Provides
    fun sharedPreference(app: Context) : SharedPreferences = app.getSharedPreferences("lavada_pref_cache", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun providePreferenceRepository(pref: SharedPreferences) : PreferenceRepository = PreferenceRepository(pref)

}