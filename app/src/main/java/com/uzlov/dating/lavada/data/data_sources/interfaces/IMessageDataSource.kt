package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface IMessageDataSource {
    suspend fun sendMessage(uidChat: String, message: ChatMessage)
    suspend fun sendMessage(uidChat: String, message: Chat)
    suspend fun getChats(userId: String) : List<Chat>
    fun createChat(selfId: String, companionId: String)
    @ExperimentalCoroutinesApi
    suspend fun observeMessages(uidChat: String): Flow<Chat>
    suspend fun getChat(uid: String): Chat
}