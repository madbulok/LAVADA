package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageViewModel @Inject constructor( var messagesRepository: IMessageDataSource) : ViewModel() {

    fun sendMessage(uidChat: String, message: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.sendMessage(uidChat, message)
        }
    }
}