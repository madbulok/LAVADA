package com.uzlov.dating.lavada.di.modules

import androidx.lifecycle.ViewModelProvider
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun viewModelFactory(factory: ViewModelFactory) : ViewModelProvider.Factory

}