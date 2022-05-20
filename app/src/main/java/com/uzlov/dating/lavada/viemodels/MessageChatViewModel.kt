package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.convertDtoToModel
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.models.*
import com.uzlov.dating.lavada.service.NewMessageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MessageChatViewModel @Inject constructor(
    var messagesRepository: IMessageDataSource,
    var useCase: ChatUseCase,
    private var userUseCases: UserUseCases,
    private val serverCommunication: ServerCommunication

) : ViewModel() {

    private val chats = MutableLiveData<List<Chat>>()
    private val chatMessages = MutableLiveData<Chat>()
    private val createdChat = MutableLiveData<String>() // store uid created chat
    private val selfUser = MutableLiveData<User>()
    private val reChat = MutableLiveData<ReChat>()
    private val reChatList = MutableLiveData<RemoteChatList>()


    // получить все чаты пользователя
    fun getChats(userId: String): LiveData<List<Chat>> {
        viewModelScope.launch(Dispatchers.IO) {

        }
        return chats
    }

    /**
     * работает
     * */
    fun getRemoteChats(token: String): LiveData<RemoteChatList> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChatList.postValue(
                serverCommunication.apiServiceWithToken?.getListChatsAsync(
                    "0",
                    "20"
                )?.await()
            )

        }
        return reChatList
    }

    private val mappedResult = MutableLiveData<List<MappedChat>>()

    fun getChats(userId: String, selfId: String): LiveData<List<MappedChat>> {
        viewModelScope.launch(Dispatchers.IO) {

        }
        return mappedResult
    }

    // создать чат с конкретным пользователем
    fun createChat(selfId: String, companionId: String): LiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {

        }
        return createdChat
    }

    /**
     * работает
     * */
    fun createRemoteChat(token: String, uidChat: String): LiveData<ReChat?> {
        val body = mutableMapOf<String, String>()
        body["to_user_id"] = uidChat
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.createChatAsync(body)?.await()
            )

        }
        return reChat
    }


    fun sendMessage(uidChat: String, chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    //не работает
    fun sendRemoteMessage(token: String, uidChat: String): LiveData<ReChat?> {
        val body = mutableMapOf<String, String>()
        body["chat_id"] = "1"
        body["text"] = "some text"
        //      body["file"] = ""
        //      body["ug_id"] = "1"
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.createMessageAsync(body)?.await()
            )
        }
        return reChat
    }

    /**
     * работает
     * */
    fun checkChat(token: String, firebase_uid: String): LiveData<ReChat?> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.checkChatAsync(firebase_uid)?.await()
            )
        }
        return reChat
    }

    fun getChatById(token: String, chatId: String): LiveData<ReChat?> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.getChatByIdAsync(chatId)?.await()
            )
        }
        return reChat
    }

    // получать сообщения из конкретного чата
    @ExperimentalCoroutinesApi
    fun retrieveMessages(uid: String): LiveData<Chat> {
        viewModelScope.launch(Dispatchers.IO) {

        }
        return chatMessages
    }

    fun observeMessage(
        uid: String,
        messageCallback: NewMessageService.NewMessageStateListener
    ) {

    }
}