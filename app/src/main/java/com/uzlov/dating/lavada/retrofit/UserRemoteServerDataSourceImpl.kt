package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.RemoteUserList
import com.uzlov.dating.lavada.domain.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class UserRemoteServerDataSourceImpl @Inject constructor(
    private val serverCommunication: ServerCommunication
    ) : IServerDataSource<Any> {

    /**пользователи*/
    override suspend fun getUser(token: String): RemoteUser {
        return serverCommunication.apiServiceWithToken?.getUserAsync()?.await()!!
    }
    override suspend fun getUserById(token: String, id: String): RemoteUser {
        return serverCommunication.apiServiceWithToken?.getUserByIdAsync(id)!!.await()
    }
    override suspend fun updateUser(token: String, field: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.updateUserAsync(field)!!.await()
    }
    override suspend fun updateData(token: String, field: MultipartBody.Part): Any {
        return serverCommunication.apiServiceWithToken?.uploadEmployeeDataAsync(field)!!.await()
    }

    override suspend fun saveUser(token: String, user: User){
        return serverCommunication.apiServiceWithToken?.saveUserAsync(token, user)!!
    }

    override suspend fun removeUser(token: String, id: String) {

    }

    override suspend fun authUser(selfToken: HashMap<String, String?>): RemoteUser {
        return serverCommunication.apiServiceWithToken?.authUserAsync(selfToken)!!.await()
    }
    override suspend fun postBalance(token: String, balance: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.postBalanceAsync(balance)!!.await()
    }
    override suspend fun getUsers(token: String): RemoteUserList {
        return serverCommunication.apiServiceWithToken?.getUsersAsync()!!.await()
    }
    override suspend fun getBalance(token: String): Any {
        return serverCommunication.apiServiceWithToken?.getUserBalanceAsync()!!.await()
    }

    override suspend fun postSubscribe(token: String, subscribe: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.postSubscribeAsync(subscribe)!!.await()
    }

    /**чаты*/
    override suspend fun createMessage(token: String, map: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.createMessageAsync(map)!!.await()
    }

    override suspend fun getMessage(token: String, chatId: String): Any {
        return serverCommunication.apiServiceWithToken?.getMessagesAsync(chatId)!!.await()
    }

    override suspend fun updateStatus(token: String, map: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.updateStatusAsync(map)!!.await()
    }

    override suspend fun getListChats(token: String, offset: String, limit: String): Any {
        return serverCommunication.apiServiceWithToken?.getListChatsAsync(offset, limit)!!.await()
    }

    override suspend fun createChat(token: String, map: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.createChatAsync(map)!!.await()
    }

    override suspend fun getChatById(token: String, chatId: String): Any {
        return serverCommunication.apiServiceWithToken?.getChatByIdAsync(chatId)!!.await()
    }

    override suspend fun checkChat(token: String, firebaseUid: String): Any {
        return serverCommunication.apiServiceWithToken?.checkChatAsync(firebaseUid)!!.await()
    }

    /**подарки*/
    override suspend fun sendGift(token: String, map: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.sendGiftAsync(map)!!.await()
    }

    override suspend fun getALlGifts(token: String): Any {
        return serverCommunication.apiServiceWithToken?.getAllGiftsAsync()!!.await()
    }

    override suspend fun postPurchase(token: String, map: Map<String, String>): Any {
        return serverCommunication.apiServiceWithToken?.postPurchaseAsync(map)!!.await()
    }

    override suspend fun getListGifts(
        token: String,
        limit: String,
        offset: String,
        status: String
    ): Any {
        return serverCommunication.apiServiceWithToken?.getListGiftsAsync(limit, offset, status)!!.await()
    }

    override suspend fun getListReceivedGifts(token: String, limit: String, offset: String): Any {
        return serverCommunication.apiServiceWithToken?.getListReceivedGiftsAsync(limit, offset)!!.await()
    }

    /**лайки*/
    override suspend fun setLike(token: String, requestBody: RequestBody): Any {
        return serverCommunication.apiServiceWithToken?.setLikeAsync(requestBody)!!.await()
    }

    override suspend fun checkLike(token: String, firebaseUid: String): Any {
        return serverCommunication.apiServiceWithToken?.checkLikeAsync(firebaseUid)!!.await()
    }

    override fun setToken(token: String) {
        serverCommunication.updateToken(token)
    }
}