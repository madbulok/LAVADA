package com.uzlov.dating.lavada.di.modules

import com.android.billingclient.api.BillingClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.data_sources.implementation.PurchasesRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.data.repository.MessagesRepository
import com.uzlov.dating.lavada.retrofit.IServerDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideApp(app: App): FirebaseApp? = Firebase.initialize(app)

    @Provides
    @Singleton
    fun providePurchaseRepository(billingBuilder: BillingClient.Builder): IPurchasesDataSource = PurchasesRemoteDataSourceImpl(billingBuilder)

    @Provides
    @Singleton
    fun provideMessageRepository(
        dataSource: IServerDataSource<Any>,
    ): IMessageDataSource = MessagesRepository(dataSource)
}