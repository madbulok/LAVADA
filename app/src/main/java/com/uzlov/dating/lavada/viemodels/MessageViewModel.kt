package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageViewModel @Inject constructor( var messagesRepository: IMessageDataSource) : ViewModel() {

    private val chatMessages = MutableLiveData<Chat>()

    fun sendMessage(uidChat: String, message: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.sendMessage(uidChat, message)
        }
    }

    fun sendMessage(uidChat: String, chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.sendMessage(uidChat, chat)
        }
    }

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