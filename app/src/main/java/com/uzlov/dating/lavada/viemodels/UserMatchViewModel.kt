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

class UserMatchViewModel @Inject constructor(var messagesRepository: IMessageDataSource, var useCase: ChatUseCase) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()

    // получить все чаты пользователя
    fun getMatches(userId: String) : LiveData<List<Chat>> {
        viewModelScope.launch {
            val result = messagesRepository.getChats(userId)
            chats.postValue(result)
        }
        return chats
    }
}