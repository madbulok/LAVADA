package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.domain.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageChatViewModel @Inject constructor(var messagesRepository: IMessageDataSource, var useCase: ChatUseCase) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()
    private val chatMessages = MutableLiveData<Chat>()
    private val createdChat = MutableLiveData<String>() // store uid created chat

    // получить все чаты пользователя
    fun getChats(userId: String) : LiveData<List<Chat>> {
        viewModelScope.launch {
            val result = messagesRepository.getChats(userId)
            chats.postValue(result)
        }
        return chats
    }

    // создать чат с конкретным пользователем
    fun createChat(selfId: String, companionId: String) : LiveData<String>{
        viewModelScope.launch(Dispatchers.IO) {
            createdChat.postValue(useCase.createChat(selfId, companionId))
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