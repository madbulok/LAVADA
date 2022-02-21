package com.uzlov.dating.lavada.di.modules

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.data_sources.IMessageDataSource
import com.uzlov.dating.lavada.data.data_sources.IRemoteDataSource
import com.uzlov.dating.lavada.data.repository.UserLocalRepository
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.data.repository.UsersRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.repository.MessagesRepository
import com.uzlov.dating.lavada.data.repository.UserRemoteRepositoryImpl
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
    fun provideRealtimeDatabase() : FirebaseDatabase = Firebase.database

    @Provides
    fun provideUserRemoteDataSource() : IRemoteDataSource = UsersRemoteDataSourceImpl()

    @Provides
    fun provideUserRepository(remoteDataSource: IRemoteDataSource) : IUsersRepository = UserRemoteRepositoryImpl(remoteDataSource)

    @Provides
    fun provideUserUseCase(localRepository: UserLocalRepository, remoteDataSource: IRemoteDataSource) : UserUseCases = UserUseCases(localRepository, remoteDataSource)

    @Provides
    fun provideMessageRepository(firebaseDatabase: FirebaseDatabase) : IMessageDataSource = MessagesRepository(firebaseDatabase)

    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage

}