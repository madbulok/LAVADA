package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: IMessageDataSource, private val userRepository: IUsersRepository) {

    // return uid key created chat
    fun createChat(selfId: String, companionId: String) : String? {
        userRepository.getUser(selfId).value?.let {
            // update links for chats user
            it.chats.put(companionId, selfId)
            userRepository.putUser(it)
            return@let chatRepository.createChat(selfId, companionId)
        }
        return null
    }

    suspend fun getChat(uid: String): Chat {
        return chatRepository.getChat(uid)
    }
}