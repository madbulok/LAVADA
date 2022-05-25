package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.data.use_cases.ChatUseCase
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.models.*
import com.uzlov.dating.lavada.service.NewMessageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val reMessage = MutableLiveData<ReMessage>()
    private val reChatList = MutableLiveData<RemoteChatList>()
    private val mappedResult = MutableLiveData<List<MappedChat>>()

    /**
     * получить список чатов пользователя
     * @param token токен от бэка
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

    /**
     * создать чат
     * @param token токен от бэка
     * @param uidUser id юзера (внутренний, бэковский id пользователя, с которым хотим создать чат)
     * */
    fun createRemoteChat(token: String, uidUser: String): LiveData<ReChat?> {
        val body = mutableMapOf<String, String>()
        body["to_user_id"] = uidUser
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.createChatAsync(body)?.await()
            )
        }
        return reChat
    }

    /**
     * отправить сообщение. возвращает статус "ок" и id сообщения
     * @param
     * @param
     * @param
     */
    fun sendRemoteMessage(token: String, uidChat: String, textMessage: String): LiveData<ReChat?> {
        val body = mutableMapOf<String, String>()
        body["chat_id"] = uidChat
        body["text"] = textMessage
        // TODO: 24.05.2022 разобраться с передачей файла
        //      body["file"] = ""
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.createMessageAsync(body)?.await()
            )
        }
        return reChat
    }
    fun sendRemoteMessage(token: String, uidChat: String, textMessage: String, ugId: String): LiveData<ReChat?> {
        val body = mutableMapOf<String, String>()
        body["chat_id"] = uidChat
        body["text"] = textMessage
        body["ug_id"] = ugId
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.createMessageAsync(body)?.await()
            )
        }
        return reChat
    }

    /**
     * проверить, существует ли чат с другим пользователем
     * @param token токен от бэка
     * @param firebaseUid uid firebase
     * */
    fun checkChat(token: String, firebaseUid: String): LiveData<ReChat?> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.checkChatAsync(firebaseUid)?.await()
            )
        }
        return reChat
    }

    /**
     * получить список сообщений из конкретного чата
     * @param token токен от бэка
     * @param chatId id чата от бэка
     * */
    fun getListMessages(token: String, chatId: String): LiveData<ReMessage>{
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
           reMessage.postValue(
                serverCommunication.apiServiceWithToken?.getMessagesAsync(chatId)?.await()
            )
        }
        return reMessage
    }

    /**
     * получить чат по Id
     * @param token токен от бэка
     * @param chatId id чата от бэка
     * */
    fun getChatById(token: String, chatId: String): LiveData<ReChat?> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reChat.postValue(
                serverCommunication.apiServiceWithToken?.getChatByIdAsync(chatId)?.await()
            )
        }
        return reChat
    }


    fun observeMessage(
        uid: String,
        messageCallback: NewMessageService.NewMessageStateListener
    ) {

    }
}