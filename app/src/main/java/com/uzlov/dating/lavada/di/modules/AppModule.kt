package com.uzlov.dating.lavada.di.modules

import android.content.Context
import com.uzlov.dating.lavada.app.App
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: App) {

    @Provides
    fun app(): Context = app

}