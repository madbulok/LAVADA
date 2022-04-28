package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.domain.models.*
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    /**пользователи**/
    //получить список пользователей +
    @GET("api/v1/users")
    fun getUsersAsync(
        //       @Query("limit") limit: String,
        //       @Query("offset") offset: String,
        //       @Query("order_by_location") order_by_location: String,
        //       @Query("only_premium") only_premium: String,
    ): Deferred<List<RemoteUser>>

    //получить пользователя +
    @GET("api/v1/user")
    fun getUserAsync() : Deferred<RemoteUser>

    //получить пользователя по uid fb +
    @GET("api/v1/user")
    fun getUserByIdAsync(
        @Query("firebase_uid") id: String
    ) : Deferred<RemoteUser>

    //получить баланс пользователя +
    @GET("api/v1/user/balance")
    fun getUserBalanceAsync(): Deferred<RemoteUser>

    //обновить данные пользователя +
    @FormUrlEncoded
    @POST("api/v1/user")
    fun updateUserAsync(
        @FieldMap() field: Map<String, String>
    ): Deferred<ResponseBody>

    //тут грузить фото/видео. пока не работает
    @Multipart
    @POST("api/v1/user")
    fun uploadEmployeeDataAsync(
        @PartMap map: HashMap<String, RequestBody>
    ): Deferred<Response<ResponseBody>>

    //авторизовать пользователя, обратно приходит токен с бэка +
    @FormUrlEncoded
    @POST("api/v1/user/auth")
    fun authUserAsync(
        @FieldMap params: HashMap<String, String?>
    ): Deferred<RemoteUser>

    //начисление баланса пользователю +
    /** зачислилось 2 раза и перестало. Хз почему, надо разбираться*/
    @FormUrlEncoded
    @POST("api/v1/user/balance")
    fun postBalanceAsync(
        @FieldMap balance: Map<String, String>
    ): Deferred<RemoteUser>

    //включение подписки. меняет статус на _has_premium":[{"google":"2022-06-10 00:00:00"}] +
    @FormUrlEncoded
    @POST("api/v1/user/subscribe")
    fun postSubscribeAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<RemoteUser>

    /**чаты**/
    @FormUrlEncoded
    @POST("api/v1/chat/message")
    fun createMessageAsync(
        @FieldMap map: Map<String, String>
    ): Deferred<ResponseBody>

    @GET("v1/chat/message/list")
    fun getMessagesAsync(
        @Query("chat_id") id: String
    ): Deferred<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/chat/message/status")
    fun updateStatusAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ResponseBody>

    @GET("api/v1/chat/list")
    fun getListChatsAsync(
        @Query("offset") offset: String,
        @Query("limit") limit: String
    ): Deferred<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/chat")
    fun createChatAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ResponseBody>

    @GET("api/v1/chat")
    fun getChatByIdAsync(
        @Query("chat_id") chat_id: String
    ): Deferred<ResponseBody>

    @GET("api/v1/chat/check")
    fun checkChatAsync(
        @Query("firebase_uid") firebase_uid: String
    ): Deferred<ResponseBody>

    /**подарки**/
    @FormUrlEncoded
    @POST("api/v1/gift/send")
    fun sendGiftAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ResponseBody>

    @GET("api/v1/gift/all")
    fun getAllGiftsAsync(): Deferred<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/gift/purchase")
    fun postPurchaseAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ResponseBody>

    @GET("api/v1/gift/purchased")
    fun getListGiftsAsync(
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("status") status: String
    ): Deferred<ResponseBody>

    @GET("api/v1/gift/donated")
    fun getListReceivedGiftsAsync(
        @Query("limit") limit: String,
        @Query("offset") offset: String
    ): Deferred<ResponseBody>


    /**лайки**/

    @PUT("api/v1/like")
    fun setLikeAsync(
        @Body requestBody: RequestBody
    ): Deferred<ResponseBody>

    @GET("api/v1/like/check")
    fun checkLikeAsync(
        @Query("firebase_uid") firebase_uid: String
    ): Deferred<ResponseBody>
}
