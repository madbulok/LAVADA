package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.data.OAuthInterceptor
import com.uzlov.dating.lavada.di.modules.RepositoryModule
import com.uzlov.dating.lavada.domain.models.RemoteUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named


class RemoteDataSource @Inject constructor(
    @field:Named(RepositoryModule.RETROFIT_WITH_TOKEN) private val apiWithToken: ApiService,
    @field:Named(RepositoryModule.RETROFIT_WITHOUT_TOKEN) private val apiWithOutToken: ApiService,
    val okHttpClient:  OkHttpClient.Builder,
    ) : DataSource<Any> {

    /**пользователи*/
    override suspend fun getUser(token: String): RemoteUser {
        return apiWithToken.getUserAsync().await()
    }
    override suspend fun getUserById(token: String, id: String): RemoteUser {
        return apiWithToken.getUserByIdAsync(id).await()
    }
    override suspend fun updateUser(token: String, field: Map<String, String>): Any {
        return apiWithToken.updateUserAsync(field).await()
    }
    override suspend fun updateData(token: String, field: HashMap<String, RequestBody>): Any {
        return apiWithToken.uploadEmployeeDataAsync(field).await()
    }

    override suspend fun authUser(selfToken: HashMap<String?, String?>): RemoteUser {
        return apiWithOutToken.authUserAsync(selfToken).await()
    }
    override suspend fun postBalance(token: String, balance: Map<String, String>): Any {
        return apiWithToken.postBalanceAsync(balance).await()
    }
    override suspend fun getUsers(token: String): List<RemoteUser> {
        return apiWithToken.getUsersAsync().await()
    }
    override suspend fun getBalance(token: String): Any {
        return apiWithToken.getUserBalanceAsync().await()
    }

    override suspend fun postSubscribe(token: String, subscribe: Map<String, String>): Any {
        return apiWithToken.postSubscribeAsync(subscribe).await()
    }

    /**чаты*/
    override suspend fun createMessage(token: String, map: Map<String, String>): Any {
        return apiWithToken.createMessageAsync(map).await()
    }

    override suspend fun getMessage(token: String, chatId: String): Any {
        return apiWithToken.getMessagesAsync(chatId).await()
    }

    override suspend fun updateStatus(token: String, map: Map<String, String>): Any {
        return apiWithToken.updateStatusAsync(map).await()
    }

    override suspend fun getListChats(token: String, offset: String, limit: String): Any {
        return apiWithToken.getListChatsAsync(offset, limit).await()
    }

    override suspend fun createChat(token: String, map: Map<String, String>): Any {
        return apiWithToken.createChatAsync(map).await()
    }

    override suspend fun getChatById(token: String, chatId: String): Any {
        return apiWithToken.getChatByIdAsync(chatId).await()
    }

    override suspend fun checkChat(token: String, firebaseUid: String): Any {
        return apiWithToken.checkChatAsync(firebaseUid).await()
    }

    /**подарки*/
    override suspend fun sendGift(token: String, map: Map<String, String>): Any {
        return apiWithToken.sendGiftAsync(map).await()
    }

    override suspend fun getALlGifts(token: String): Any {
        return apiWithToken.getAllGiftsAsync().await()
    }

    override suspend fun postPurchase(token: String, map: Map<String, String>): Any {
        return apiWithToken.postPurchaseAsync(map).await()
    }

    override suspend fun getListGifts(
        token: String,
        limit: String,
        offset: String,
        status: String
    ): Any {
        return apiWithToken.getListGiftsAsync(limit, offset, status).await()
    }

    override suspend fun getListReceivedGifts(token: String, limit: String, offset: String): Any {
        return apiWithToken.getListReceivedGiftsAsync(limit, offset).await()
    }

    /**лайки*/
    override suspend fun setLike(token: String, requestBody: RequestBody): Any {
        return apiWithToken.setLikeAsync(requestBody).await()
    }

    override suspend fun checkLike(token: String, firebaseUid: String): Any {
        return apiWithToken.checkLikeAsync(firebaseUid).await()
    }

    // желательно этим методом не пользоваться, нарушает инкапсуляцию
    override fun getClient():  OkHttpClient.Builder {
        return okHttpClient
    }

    override fun clearInterceptors() {
        okHttpClient.interceptors().clear()
    }

    override fun addInterceptor(interceptor: Interceptor) {
        okHttpClient.addInterceptor(interceptor)
    }

    override fun setToken(token: String, isClearOldest: Boolean) {
        if (isClearOldest) clearInterceptors()
        okHttpClient.addInterceptor(OAuthInterceptor("Bearer", token))
    }
}