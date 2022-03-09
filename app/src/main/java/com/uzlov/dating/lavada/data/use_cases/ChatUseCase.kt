package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: IMessageDataSource, private val userRepository: IUsersRepository) {

    fun createChat(selfId: String, companionId: String){
        userRepository.getUser(selfId).value?.let {
            // update links for chats user
            it.chats[companionId] = selfId
            userRepository.putUser(it)

            chatRepository.createChat(selfId, companionId)
        }
    }

    suspend fun getChat(uid: String): Chat {
        return chatRepository.getChat(uid)
    }
}