package com.uzlov.dating.lavada.di.modules

import com.uzlov.dating.lavada.data.data_sources.implementation.GiftRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.implementation.SubscriptionsRemoteDataSourceImpl
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.ISubscriptionsDataSource
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.retrofit.IServerDataSource
import com.uzlov.dating.lavada.retrofit.UserRemoteServerDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    // data sources and etc.
    @Provides
    @Singleton
    fun provideDataSourceRemote(
        serverCommunication: ServerCommunication
    ): IServerDataSource<Any> = UserRemoteServerDataSourceImpl(serverCommunication)


    @Provides
    fun provideUserUseCase(iRemoteDataSource:  IServerDataSource<Any>): UserUseCases =
        UserUseCases(iRemoteDataSource)

    @Provides
    fun provideChatUseCases(
        messageRepository: IMessageDataSource
    ): ChatUseCase = ChatUseCase(messageRepository)

    @Provides
    @Singleton
    fun provideGiftDataSource(datasource: IServerDataSource<Any>) : IGiftsDataSource = GiftRemoteDataSourceImpl(datasource)

    @Provides
    @Singleton
    fun provideSubscriptionsDataSource() : ISubscriptionsDataSource = SubscriptionsRemoteDataSourceImpl()


    @Provides
    @Singleton
    fun provideServerCommunication() : ServerCommunication = ServerCommunication.create("")
}