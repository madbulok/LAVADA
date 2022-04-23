package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import com.uzlov.dating.lavada.service.NewMessageService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface IMessageDataSource {
    suspend fun sendMessage(uidChat: String, message: ChatMessage)
    suspend fun sendMessage(uidChat: String, message: Chat)
    suspend fun getChats(userId: String) : List<Chat>
    fun createChat(selfId: String, companionId: String) : String

    @ExperimentalCoroutinesApi
    suspend fun observeMessages(uidChat: String): Flow<Chat>
    suspend fun getChat(uid: String): Chat
    suspend fun getChat(companionId: String, selfId: String): Chat
    suspend fun hasChat(companionId: String, selfId: String) : Boolean
    fun observeNewMessages(uidChat: String, messageCallback: NewMessageService.NewMessageStateListener)

    /**чаты*/
    suspend fun createMessage(token: String, map: Map<String, String>)
    suspend fun getMessage(token: String, chatId: String)
    suspend fun updateStatus(token: String, map: Map<String, String>)
    suspend fun getListChats(token: String, offset: String, limit: String)
    suspend fun createRemoteChat(token: String, map: Map<String, String>)
    suspend fun getChatById(token: String, chatId: String)
    suspend fun checkChat(token: String, firebaseUid: String)
}