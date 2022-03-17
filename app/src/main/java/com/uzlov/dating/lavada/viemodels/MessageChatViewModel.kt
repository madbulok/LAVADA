package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.*
import com.uzlov.dating.lavada.app.getCompanionUid
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.repository.UserRemoteRepositoryImpl
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.MappedChat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageChatViewModel @Inject constructor(var messagesRepository: IMessageDataSource, var useCase: ChatUseCase, var userRemoteRepositoryImpl: IUsersRepository) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()
    private val chatMessages = MutableLiveData<Chat>()
    private val createdChat = MutableLiveData<String>() // store uid created chat

    // получить все чаты пользователя
    fun getChats(userId: String) : LiveData<List<Chat>> {
        viewModelScope.launch(Dispatchers.IO) {
            val result = messagesRepository.getChats(userId)
            chats.postValue(result)
        }
        return chats
    }

    private val mappedResult = MutableLiveData<List<MappedChat>>()
    fun getChats(userId: String, selfId: String) : LiveData<List<MappedChat>> {
        viewModelScope.launch(Dispatchers.IO) {
            val result = messagesRepository.getChats(userId)
            val mapped = result.map { chat->
                MappedChat(userRemoteRepositoryImpl.getUser(chat.getCompanionUid(selfId))!!, chat)
            }
            Log.e("TAG", "getChats: $mapped")
            mappedResult.postValue(mapped)
        }
        return mappedResult
    }

    // создать чат с конкретным пользователем
    fun createChat(selfId: String, companionId: String) : LiveData<String>{
        viewModelScope.launch(Dispatchers.IO) {
            useCase.createChat(selfId, companionId).let { uid ->
                createdChat.postValue(uid)
            }
        }
        return createdChat
    }

    // отправить сообщение
    fun sendMessage(uidChat: String, chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.sendMessage(uidChat, chat)
        }
    }

    // получать сообщения из конкретного чата
    @ExperimentalCoroutinesApi
    fun retrieveMessages(uid: String) : LiveData<Chat> {
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.observeMessages(uid)
                .collect {
                    chatMessages.postValue(it)
                }
        }
        return chatMessages
    }
}