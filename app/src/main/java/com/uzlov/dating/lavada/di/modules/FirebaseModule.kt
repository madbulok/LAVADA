package com.uzlov.dating.lavada.di.modules

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.data_sources.implementation.GiftRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.implementation.PurchasesRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.data.repository.MessagesRepository
import com.uzlov.dating.lavada.retrofit.RemoteIDataSource
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
    fun provideGiftRepository(remoteDataSource: RemoteIDataSource): IGiftsDataSource =
        GiftRemoteDataSourceImpl(remoteDataSource)

    @Provides
    @Singleton
    fun providePurchaseRepository(): IPurchasesDataSource = PurchasesRemoteDataSourceImpl()

    @Provides
    fun provideMessageRepository(
        remoteDataSource: RemoteIDataSource,
    ): IMessageDataSource = MessagesRepository(remoteDataSource)

    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage
}