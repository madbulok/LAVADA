package com.uzlov.dating.lavada.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzlov.dating.lavada.viemodels.MessageViewModel
import com.uzlov.dating.lavada.viemodels.ViewModelFactory
import com.uzlov.dating.lavada.viemodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun viewModelFactory(factory: ViewModelFactory) : ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MessageViewModel::class)
    abstract fun provideMessageViewModel(messageViewModel: MessageViewModel) : ViewModel
}