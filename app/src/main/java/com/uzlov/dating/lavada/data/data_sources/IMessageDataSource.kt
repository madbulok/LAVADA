package com.uzlov.dating.lavada.data.data_sources

import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage

interface IMessageDataSource {
    suspend fun sendMessage(uidChat: String, message: ChatMessage)
    suspend fun observeMessages(uidChat: String): ChatMessage
    suspend fun getChats(userId: String) : List<Chat>
}