package com.uzlov.dating.lavada.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody

interface DataSource<T> {
    /**пользователи*/
    suspend fun getUser(token: String): T
    suspend fun getUserById(token: String, id: String): T
    suspend fun authUser(selfToken: HashMap<String?, String?>): T
    suspend fun getUsers(token: String): List<T>
    suspend fun getBalance(token: String): T
    suspend fun updateUser(token: String, field: Map<String, String>): T
    suspend fun updateData(token: String, field: HashMap<String, RequestBody>): T
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
    suspend fun setLike(token: String, requestBody: RequestBody): T
    suspend fun checkLike(token: String, firebaseUid: String): T

    fun getClient() :  OkHttpClient.Builder
    fun clearInterceptors()
    fun addInterceptor(interceptor: Interceptor)
    fun setToken(token: String, isClearOldest: Boolean = false)
}