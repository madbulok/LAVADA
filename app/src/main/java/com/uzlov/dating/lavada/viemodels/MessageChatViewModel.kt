package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.*
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.models.*
import com.uzlov.dating.lavada.service.NewMessageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageChatViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val serverCommunication: ServerCommunication

) : ViewModel() {

    private val reChatCreated = MutableLiveData<ReChat>()
    private val messageSend = MutableLiveData<ReChat>()
    private val reMessages = MutableLiveData<List<ChatMessage>>()
    private val reChatList = MutableLiveData<List<MappedChat>>()
    private val giftMessageSend = MutableLiveData<ReChat>()
    private val checkChat = MutableLiveData<ReChat>()
    private val reChatById = MutableLiveData<ReChat>()
    private val companion = MutableLiveData<String>()

    val status = MutableLiveData<String?>()
    val chatListData get(): MutableLiveData<List<MappedChat>> = reChatList
    val chatCreatedData get(): MutableLiveData<ReChat> = reChatCreated
    val messageSendData get(): MutableLiveData<ReChat> = messageSend
    val giftMessageSendData get(): MutableLiveData<ReChat> = giftMessageSend
    val checkChatData get(): MutableLiveData<ReChat> = checkChat
    val listMessageData get(): MutableLiveData<List<ChatMessage>> = reMessages
    val reChatByIdData get(): MutableLiveData<ReChat> = reChatById
    val companionData get(): MutableLiveData<String> = companion

    /**
     * получить список чатов пользователя
     * @param token токен от бэка
     * */
    fun getRemoteChats(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser =
                        serverCommunication.apiServiceWithToken?.getListChatsAsync("0", "20")
                    reUser.let { response ->
                        if (response!!.isSuccessful) {
                            val mappedChat = mutableListOf<MappedChat>()
                            val list = response.body()?.data?.rows
                            if (list != null) {
                                for (chat in list) {
                                    /**
                                     * тут нужно будет получить юзера по id, и загрузить сообщения*/

                                    val _chat = convertReChatToChat(chat!!)
                                    val user1 = _chat.members[0]
                                    val user2 = _chat.members[1]

                                    serverCommunication.apiServiceWithToken?.getUserAsync().let { response ->
                                        if (reUser!!.isSuccessful) {
                                            if (response!!.body()?.data?.user_id == user1){
                                                serverCommunication.apiServiceWithToken?.getUserByLavadaIdAsync(user2).let {
                                                    if (it != null) {
                                                      val companion = it.body()
                                                            ?.let { it1 -> convertDtoToModel(it1) }!!
                                                        mappedChat.add(convertChatToMappedChat(_chat, companion))
                                                    }
                                                }
                                            } else{
                                                serverCommunication.apiServiceWithToken?.getUserByLavadaIdAsync(user1).let {
                                                    if (it != null) {
                                                       val companion = it.body()
                                                            ?.let { it1 -> convertDtoToModel(it1) }!!
                                                        mappedChat.add(convertChatToMappedChat(_chat, companion))
                                                    }
                                                }
                                            }


                                        } else {
                                            status.postValue(displayApiResponseErrorBody(reUser))
                                        }
                                    }
                                }
                            }

                            reChatList.postValue(mappedChat)
                        } else {
                            status.postValue(displayApiResponseErrorBody(response))
                        }
                    }
                }
            }
        }
    }


    /**
     * создать чат
     * @param token токен от бэка
     * @param uidUser id юзера (внутренний, бэковский id пользователя, с которым хотим создать чат)
     * */
    fun createRemoteChat(token: String, uidUser: String) {
        val body = mutableMapOf<String, String>()
        body["to_user_id"] = uidUser
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.createChatAsync(body)
                        ?.let { response ->
                            if (response.isSuccessful) {
                                reChatCreated.postValue(response.body())
                            } else {
                                status.postValue(displayApiResponseErrorBody(response))
                            }
                        }
                }
            }
        }
    }

    /**
     * отправить сообщение. возвращает статус "ок" и id сообщения
     * @param
     * @param
     * @param
     */
    fun sendRemoteMessage(token: String, uidChat: String, textMessage: String) {
        val body = mutableMapOf<String, String>()
        body["chat_id"] = uidChat
        body["text"] = textMessage
        // TODO: 24.05.2022 разобраться с передачей файла
        //      body["file"] = ""
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.createMessageAsync(body)
                        ?.let { reUser ->
                            if (reUser.isSuccessful) {
                                messageSend.postValue(
                                    reUser.body()
                                )
                            } else {
                                status.postValue(displayApiResponseErrorBody(reUser))
                            }
                        }
                }
            }
        }
    }

    fun sendRemoteGiftMessage(token: String, uidChat: String, ugId: String) {
        val body = mutableMapOf<String, String>()
        body["chat_id"] = uidChat
        body["ug_id"] = ugId
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.createMessageAsync(body)
                        ?.let { reUser ->
                            if (reUser.isSuccessful) {
                                giftMessageSend.postValue(
                                    reUser.body()
                                )
                            } else {
                                status.postValue(displayApiResponseErrorBody(reUser))
                            }
                        }
                }
            }
        }
    }

    /**
     * проверить, существует ли чат с другим пользователем
     * @param token токен от бэка
     * @param firebaseUid uid firebase
     * */
    fun checkChat(token: String, firebaseUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.checkChatAsync(firebaseUid)
                        ?.let { reUser ->
                            if (reUser.isSuccessful) {
                                checkChat.postValue(
                                    reUser.body()
                                )
                            } else {
                                status.postValue(displayApiResponseErrorBody(reUser))
                            }
                        }
                }
            }
        }
    }

    /**
     * получить список сообщений из конкретного чата
     * @param token токен от бэка
     * @param chatId id чата от бэка
     * */
    fun getListMessages(token: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                    val result =
                        serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                    result?.body()?.data?.token?.let { tokenToo ->
                        serverCommunication.updateToken(tokenToo)
                        val reUser =
                            serverCommunication.apiServiceWithToken?.getMessagesAsync(chatId)
                        reUser.let { response ->
                            if (response!!.isSuccessful) {
                                val listMessage = mutableListOf<ChatMessage>()
                                val list = response.body()?.data?.rows
                                if (list != null) {
                                    for (remoteMessage in list) {
                                        listMessage.add(
                                            convertListReMessageToChatMessage(
                                                remoteMessage!!
                                            )
                                        )
                                    }
                                    listMessage.reverse()
                                    reMessages.postValue(listMessage)
                                } else {
                                    status.postValue(displayApiResponseErrorBody(response))
                                }
                            }
                        }
                    }
                }
                delay(5000)
            }

        }
    }


    /**
     * получить чат по Id
     * @param token токен от бэка
     * @param chatId id чата от бэка
     * */
    fun getChatById(token: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reChat =
                        serverCommunication.apiServiceWithToken?.getChatByIdAsync(chatId)
                    reChat.let { response ->
                        if (response!!.isSuccessful) {
                            val resChat = response.body()
                          val self = serverCommunication.apiServiceWithToken?.getUserAsync()
                            if (self != null) {
                                if (response.body()?.data?.chat_to_user_id != self.body()?.data?.user_id){
                                    companion.postValue(response.body()!!.data?.chat_to_user_id!!)
                                } else{
                                    companion.postValue(response.body()!!.data?.chat_user_id!!)
                                }
                                    reChatById.postValue(resChat!!)
                            }
                        } else {
                            status.postValue(displayApiResponseErrorBody(response))
                        }
                    }
                }
            }
        }
    }


    fun observeMessage(
        uid: String,
        messageCallback: NewMessageService.NewMessageStateListener
    ) {

    }
}