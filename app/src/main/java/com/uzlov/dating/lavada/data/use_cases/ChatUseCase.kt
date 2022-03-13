package com.uzlov.dating.lavada.data.use_cases

import android.util.Log
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import java.lang.RuntimeException
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: IMessageDataSource, private val userRepository: IUsersRepository) {

    private val tag = javaClass.simpleName

    /**
     * Создает чат между двумя собеседниками, но сначала проверяет
     * на случай если чат уже существует.
     *
     * @return uid chat if exists else uid new created chat
     */
    suspend fun createChat(selfId: String, companionId: String) : String {
        // save relation for self
        val user = userRepository.getUser(selfId)
        if (user != null){
            user.chats.put(companionId, selfId)
            userRepository.putUser(user)

            //self relation for companion
            val companionUser = userRepository.getUser(companionId)
            if (companionUser != null){
                companionUser.chats.put(companionId, selfId)
                userRepository.putUser(companionUser)
            }

            val hasChat = hasChat(companionId, selfId)
            return if (!hasChat){
                Log.e(tag, "create new chat between $companionId, $selfId")
                chatRepository.createChat(selfId, companionId)
            } else {
                Log.e(tag, "getting exists chat between $companionId, $selfId")
                getChat(selfId, companionId).uuid
            }
        }
        return ""
    }

    suspend fun getChat(uid: String): Chat {
        return chatRepository.getChat(uid)
    }

    /**
     * Ищет чат с указанными собеседниками
     * @return Chat if exists
     * @throws RuntimeException if chat not found
     */
    suspend fun hasChat(selfId: String, companionId: String) : Boolean {
        return chatRepository.hasChat(companionId, selfId)
    }

    /**
     * Ищет чат с указанными собеседниками
     * @return Chat if exists
     * @throws RuntimeException if chat not found
     */
    suspend fun getChat(selfId: String, companionId: String) : Chat {
        return chatRepository.getChat(companionId, selfId)
    }
}