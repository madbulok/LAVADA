package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.domain.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(var messagesRepository: IMessageDataSource, var useCase: ChatUseCase) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()
    private val chat = MutableLiveData<Chat>()

    fun observeChat(userId: String) : LiveData<List<Chat>> {
        viewModelScope.launch {
            val result = messagesRepository.getChats(userId)
            chats.postValue(result)
        }
        return chats
    }

    fun createChat(selfId: String, companionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            useCase.createChat(selfId, companionId)
        }
    }

    fun getChat(uid: String): LiveData<Chat>{
        viewModelScope.launch(Dispatchers.IO) {
            chat.postValue(useCase.getChat(uid))
        }
        return chat
    }
}