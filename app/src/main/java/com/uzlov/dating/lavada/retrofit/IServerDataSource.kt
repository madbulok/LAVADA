package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.domain.models.User
import okhttp3.MultipartBody

interface IServerDataSource<T> {
    /**пользователи*/
    suspend fun getUser(token: String): T
    suspend fun getUserById(token: String, id: String): T
    suspend fun authUser(selfToken: HashMap<String, String?>): T
    suspend fun getUsers(token: String): T
    suspend fun getBalance(token: String): T
    suspend fun updateUser(token: String, field: Map<String, String>): T
    suspend fun updateData(token: String, field: MultipartBody.Part): T
    suspend fun saveUser(token: String, user: User)
    suspend fun removeUser(token: String, id: String)
    suspend fun postBalance(token: String, balance: Map<String, String>): T
    suspend fun postSubscribe(token: String, subscribe: Map<String, String>): T

    /**чаты*/
    suspend fun createMessage(token: String, map: Map<String, String>): T
    suspend fun getMessage(token: String, chatId: String): T
    suspend fun updateStatus(token: String, map: Map<String, String>): T
    suspend fun getListChats(token: String, offset: String, limit: String): T
    suspend fun createChat(token: String, map: Map<String, String>): T
    suspend fun getChatById(token: String, chatId: String): T
    suspend fun checkChat(token: String, firebaseUid: String): T

    /**подарки*/
    suspend fun sendGift(token: String, map: Map<String, String>): T
    suspend fun getALlGifts(token: String): T
    suspend fun postPurchase(token: String, map: Map<String, String>): T
    suspend fun getListGifts(token: String, limit: String, offset: String, status: String): T
    suspend fun getListReceivedGifts(token: String, limit: String, offset: String): T

    /**лайки*/
    suspend fun setLike(token: String, like: String, status: String): T
    suspend fun checkLike(token: String, firebaseUid: String): T

    fun setToken(token: String)
}