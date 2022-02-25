package com.uzlov.dating.lavada.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzlov.dating.lavada.viemodels.*
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

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun provideChatViewModel(chatViewModel: ChatViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PurchasesViewModel::class)
    abstract fun purchasesViewModel(purchasesViewModel: PurchasesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GiftsViewModels::class)
    abstract fun giftsViewModels(giftsViewModels: GiftsViewModels) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SubscriptionsViewModel::class)
    abstract fun subscriptionsViewModel(subscriptionsViewModel: SubscriptionsViewModel) : ViewModel
}