package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.retrofit.IServerDataSource
import com.uzlov.dating.lavada.retrofit.UserRemoteServerDataSourceImpl
import javax.inject.Inject

/**
 * Отправка и получение сообщений
 */
class MessagesRepository @Inject constructor(private val userRemoteDataSourceImpl: IServerDataSource<Any>) : IMessageDataSource {

    override suspend fun createMessage(token: String, map: Map<String, String>) {
        userRemoteDataSourceImpl.createMessage(token, map)
    }

    override suspend fun getMessage(token: String, chatId: String) {
        userRemoteDataSourceImpl.getMessage(token, chatId)
    }

    override suspend fun updateStatus(token: String, map: Map<String, String>) {
        userRemoteDataSourceImpl.updateStatus(token, map)
    }

    override suspend fun getListChats(token: String, offset: String, limit: String) {
        userRemoteDataSourceImpl.getListChats(token, offset, limit)
    }

    override suspend fun createRemoteChat(token: String, map: Map<String, String>) {
        userRemoteDataSourceImpl.createChat(token, map)
    }

    override suspend fun getChatById(token: String, chatId: String) {
        userRemoteDataSourceImpl.getChatById(token, chatId)
    }

    override suspend fun checkChat(token: String, firebaseUid: String) {
        userRemoteDataSourceImpl.checkChat(token, firebaseUid)
    }
}