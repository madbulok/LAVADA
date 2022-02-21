package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(var messagesRepository: IMessageDataSource) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()

    fun observeChat(userId: String) : LiveData<List<Chat>> {
        viewModelScope.launch(Dispatchers.IO) {
            val result = messagesRepository.getChats(userId)
            chats.postValue(result)
        }
        return chats
    }
}