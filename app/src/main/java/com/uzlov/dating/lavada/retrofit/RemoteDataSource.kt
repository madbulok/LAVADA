package com.uzlov.dating.lavada.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.uzlov.dating.lavada.domain.models.RemoteUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://vm3355680.52ssd.had.wf/"

class RemoteDataSource : DataSource<Any> {

    /**пользователи*/
    override suspend fun getUser(token: String): RemoteUser {
        return getService(BaseInterceptor.interceptor, token).getUserAsync().await()
    }
    override suspend fun getUserById(token: String, id: String): RemoteUser {
        return getService(BaseInterceptor.interceptor, token).getUserByIdAsync(id).await()
    }
    override suspend fun updateUser(token: String, field: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).updateUserAsync(field).await()
    }
    override suspend fun updateData(token: String, field: HashMap<String, RequestBody>): Any {
        return getService(BaseInterceptor.interceptor, token).uploadEmployeeDataAsync(field).await()
    }

    override suspend fun authUser(selfToken: HashMap<String?, String?>): RemoteUser {
        return getService(BaseInterceptor.interceptor).authUserAsync(selfToken).await()
    }
    override suspend fun postBalance(token: String, balance: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).postBalanceAsync(balance).await()
    }
    override suspend fun getUsers(token: String): List<RemoteUser> {
        return getService(BaseInterceptor.interceptor, token).getUsersAsync().await()
    }
    override suspend fun getBalance(token: String): Any {
        return getService(BaseInterceptor.interceptor, token).getUserBalanceAsync().await()
    }

    override suspend fun postSubscribe(token: String, subscribe: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).postSubscribeAsync(subscribe).await()
    }

    /**чаты*/
    override suspend fun createMessage(token: String, map: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).createMessageAsync(map).await()
    }

    override suspend fun getMessage(token: String, chatId: String): Any {
        return getService(BaseInterceptor.interceptor, token).getMessagesAsync(chatId).await()
    }

    override suspend fun updateStatus(token: String, map: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).updateStatusAsync(map).await()
    }

    override suspend fun getListChats(token: String, offset: String, limit: String): Any {
        return getService(BaseInterceptor.interceptor, token).getListChatsAsync(offset, limit).await()
    }

    override suspend fun createChat(token: String, map: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).createChatAsync(map).await()
    }

    override suspend fun getChatById(token: String, chatId: String): Any {
        return getService(BaseInterceptor.interceptor, token).getChatByIdAsync(chatId).await()
    }

    override suspend fun checkChat(token: String, firebaseUid: String): Any {
        return getService(BaseInterceptor.interceptor, token).checkChatAsync(firebaseUid).await()
    }

    /**подарки*/
    override suspend fun sendGift(token: String, map: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).sendGiftAsync(map).await()
    }

    override suspend fun getALlGifts(token: String): Any {
        return getService(BaseInterceptor.interceptor, token).getAllGiftsAsync().await()
    }

    override suspend fun postPurchase(token: String, map: Map<String, String>): Any {
        return getService(BaseInterceptor.interceptor, token).postPurchaseAsync(map).await()
    }

    override suspend fun getListGifts(
        token: String,
        limit: String,
        offset: String,
        status: String
    ): Any {
        return getService(BaseInterceptor.interceptor, token).getListGiftsAsync(limit, offset, status).await()
    }

    override suspend fun getListReceivedGifts(token: String, limit: String, offset: String): Any {
        return getService(BaseInterceptor.interceptor, token).getListReceivedGiftsAsync(limit, offset).await()
    }

    /**лайки*/
    override suspend fun setLike(token: String, requestBody: RequestBody): Any {
        return getService(BaseInterceptor.interceptor, token).setLikeAsync(requestBody).await()
    }

    override suspend fun checkLike(token: String, firebaseUid: String): Any {
        return getService(BaseInterceptor.interceptor, token).checkLikeAsync(firebaseUid).await()
    }


    private fun getService(interceptor: Interceptor, token: String): ApiService {
        return createRetrofit(interceptor, token).create(ApiService::class.java)
    }
    private fun getService(interceptor: Interceptor): ApiService {
        return createRetrofit(interceptor).create(ApiService::class.java)
    }



    private fun createRetrofit(interceptor: Interceptor, token: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createOkHttpClient(interceptor, token))
            .build()
    }
    private fun createRetrofit(interceptor: Interceptor): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(createOkHttpClient(interceptor))
            .build()
    }

    private fun createOkHttpClient(interceptor: Interceptor, token: String): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(OAuthInterceptor("Bearer", token))
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }
    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    class OAuthInterceptor(private val tokenType: String, private val accessToken: String): Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request = request.newBuilder().header("Authorization", "$tokenType $accessToken").build()
            return chain.proceed(request)
        }
    }


}