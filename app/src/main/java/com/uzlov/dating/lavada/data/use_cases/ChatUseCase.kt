package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: IMessageDataSource) {

    private val tag = javaClass.simpleName

    /**чаты*/
    suspend fun createMessage(token: String, map: Map<String, String>) = chatRepository.createMessage(token, map)
    suspend fun getMessage(token: String, chatId: String) = chatRepository.getMessage(token, chatId)
    suspend fun updateStatus(token: String, map: Map<String, String>) = chatRepository.updateStatus(token, map)
    suspend fun getListChats(token: String, offset: String, limit: String) = chatRepository.getListChats(token, offset, limit)
    suspend fun createRemoteChat(token: String, map: Map<String, String>) = chatRepository.createRemoteChat(token, map)
    suspend fun getChatById(token: String, chatId: String) = chatRepository.getChatById(token, chatId)
    suspend fun checkChat(token: String, firebaseUid: String) = chatRepository.checkChat(token, firebaseUid)
}