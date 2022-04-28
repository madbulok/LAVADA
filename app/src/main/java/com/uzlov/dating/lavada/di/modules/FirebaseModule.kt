package com.uzlov.dating.lavada.di.modules

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.data.data_sources.implementation.GiftRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.implementation.PurchasesRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.implementation.UsersRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.interfaces.*
import com.uzlov.dating.lavada.data.repository.*
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.retrofit.RemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    fun provideAuth() : FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideApp(app: App) : FirebaseApp? = Firebase.initialize(app)

    @Singleton
    @Provides
    fun provideRealtimeDatabase() : FirebaseDatabase = FirebaseDatabase.getInstance("https://lavada-7777-default-rtdb.europe-west1.firebasedatabase.app/")

    @Provides
    fun provideUserRemoteDataSource(remoteDataSource: RemoteDataSource) : IRemoteDataSource = UsersRemoteDataSourceImpl(remoteDataSource)

    @Provides
    fun provideUserUseCase(userRepository: IRemoteDataSource) : UserUseCases = UserUseCases(userRepository)

    @Provides
    fun provideGiftRepository(remoteDataSource: RemoteDataSource) : IGiftsDataSource = GiftRemoteDataSourceImpl(
        remoteDataSource)

    @Provides
    @Singleton
    fun providePurchaseRepository(db: FirebaseDatabase) : IPurchasesDataSource = PurchasesRemoteDataSourceImpl(db)

    @Provides
    fun provideMessageRepository(db: FirebaseDatabase, remoteDataSource: RemoteDataSource) : IMessageDataSource = MessagesRepository(db, remoteDataSource)

    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage

    @Provides
    fun provideChatUseCases(chatRepository: IMessageDataSource, iRemoteDataSource: IRemoteDataSource): ChatUseCase = ChatUseCase(chatRepository, iRemoteDataSource)

}