package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.retrofit.RemoteIDataSource
import javax.inject.Inject

/**
 * Отправка и получение сообщений
 */
class MessagesRepository @Inject constructor(val remoteDataSource: RemoteIDataSource) : IMessageDataSource {

    override suspend fun createMessage(token: String, map: Map<String, String>) {
        remoteDataSource.createMessage(token, map)
    }

    override suspend fun getMessage(token: String, chatId: String) {
        remoteDataSource.getMessage(token, chatId)
    }

    override suspend fun updateStatus(token: String, map: Map<String, String>) {
        remoteDataSource.updateStatus(token, map)
    }

    override suspend fun getListChats(token: String, offset: String, limit: String) {
        remoteDataSource.getListChats(token, offset, limit)
    }

    override suspend fun createRemoteChat(token: String, map: Map<String, String>) {
        remoteDataSource.createChat(token, map)
    }

    override suspend fun getChatById(token: String, chatId: String) {
        remoteDataSource.getChatById(token, chatId)
    }

    override suspend fun checkChat(token: String, firebaseUid: String) {
        remoteDataSource.checkChat(token, firebaseUid)
    }
}