package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.domain.models.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    /**пользователи**/
    //получить список пользователей (пока без сортировки по дистанции) +
    @GET("api/v1/users")
    suspend fun getUsersAsync(
               @Query("limit") limit: String,
        //       @Query("offset") offset: String,
        //       @Query("order_by_location") order_by_location: String,
        //       @Query("only_premium") only_premium: String,
    ): Response<RemoteUserList>

    //получить пользователя +
    @GET("api/v1/user")
    suspend fun getUserAsync() : Response<RemoteUser>

    //получить пользователя по uid fb,
    //пока не используется
    @GET("api/v1/user")
    suspend fun getUserByIdAsync(
        @Query("firebase_uid") id: String
    ) : Response<RemoteUser>

    @GET("api/v1/user")
    suspend fun getUserByLavadaIdAsync(
        @Query("user_id") id: String
    ) : Response<RemoteUser>

    //получить баланс пользователя
    @GET("api/v1/user/balance")
    suspend fun getUserBalanceAsync(): Response<RemoteUser>

    //обновить данные пользователя
    @FormUrlEncoded
    @POST("api/v1/user")
    suspend fun updateUserAsync(
        @FieldMap() field: Map<String, String>
    ): Response<RemoteUser>

    @FormUrlEncoded
    @POST("api/v1/user")
    suspend fun saveUserAsync(token: String, @FieldMap() user: User)

    //тут грузить фото/видео //фото+, видео +
    @Multipart
    @POST("api/v1/user")
    suspend fun uploadEmployeeDataAsync(
        @Part body: MultipartBody.Part
    ): Response<RemoteUser>

    //авторизовать пользователя, обратно приходит токен с бэка +
    @FormUrlEncoded
    @POST("api/v1/user/auth")
    suspend fun authUserAsync(
        @FieldMap params: HashMap<String, String?>
    ): Response<RemoteUser>

    //начисление баланса пользователю
    /** зачислилось 2 раза и перестало. Хз почему, надо разбираться*/
    @FormUrlEncoded
    @POST("api/v1/user/balance")
    suspend fun postBalanceAsync(
        @FieldMap balance: Map<String, String>
    ): Response<RemoteUser>

    //включение подписки. меняет статус на _has_premium":[{"google":"2022-06-10 00:00:00"}]
    @FormUrlEncoded
    @POST("api/v1/user/subscribe")
    suspend fun postSubscribeAsync(
        @FieldMap subscribe: Map<String, String>
    ): Response<RemoteUser>

    /**чаты**/
    //создать сообщение +
    @FormUrlEncoded
    @POST("api/v1/chat/message")
    suspend fun createMessageAsync(
        @FieldMap map: Map<String, String>
    ): Response<ReChat>


    // получить список сообщений +
    @GET("api/v1/chat/message/list")
    suspend fun getMessagesAsync(
        @Query("chat_id") id: String
    ): Response<ReMessage>

    //обновить статус - прочитано/не прочитано - не могу проверить, потому что не могу создать сообщение
    @FormUrlEncoded
    @POST("api/v1/chat/message/status")
    suspend fun updateStatusAsync(
        @FieldMap subscribe: Map<String, String>
    ): Response<ResponseBody>

    //получить список чатов +
    @GET("api/v1/chat/list")
    suspend fun getListChatsAsync(
        @Query("offset") offset: String,
        @Query("limit") limit: String
    ): Response<RemoteChatList>

    // создать чат +
    @FormUrlEncoded
    @POST("api/v1/chat")
    suspend fun createChatAsync(
        @FieldMap subscribe: Map<String, String>
    ): Response<ReChat>

    //получить чат по id + (работает возможно неправильно со стороны бэка)
    @GET("api/v1/chat")
    suspend fun getChatByIdAsync(
        @Query("chat_id") chat_id: String
    ): Response<ReChat>

    //проверить, существует ли чат +
    @GET("api/v1/chat/check")
    suspend fun checkChatAsync(
        @Query("firebase_uid") firebase_uid: String
    ): Response<ReChat>

    /**подарки**/

    //подарить подарок +
    @FormUrlEncoded
    @POST("api/v1/gift/send")
    fun sendGiftAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ReGift>


    // получить список подарков +
    @GET("api/v1/gift/all")
    fun getAllGiftsAsync(): Deferred<ReGift>

    //купить подарок +
    @FormUrlEncoded
    @POST("api/v1/gift/purchase")
    fun postPurchaseAsync(
        @FieldMap subscribe: Map<String, String>
    ): Deferred<ReGift>

    //список купленных подарков +
    @GET("api/v1/gift/purchased")
    fun getListGiftsAsync(
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("status") status: String
    ): Deferred<ReGift>

    //список подарков, которые подарили пользователю +
    @GET("api/v1/gift/donated")
    fun getListReceivedGiftsAsync(
        @Query("limit") limit: String,
        @Query("offset") offset: String
    ): Deferred<ReGift>

    /**лайки +
     **/

    @PUT("api/v1/like")
    suspend fun setLike(
        @Query("firebase_uid") uid: String,
        @Query("like_state") like: String
    ): Response<RemoteUser>

    @GET("api/v1/like/check")
    suspend fun checkLikeAsync(
        @Query("firebase_uid") firebase_uid: String
    ): Response<RemoteUser>

    /**
     * черный список
     * */

    //получить черный список
    @GET("api/v1/blacklist")
    suspend fun getBlackList() : Response<RemoteUserList>

    // добавить/удалить из черного списка
    @PUT("api/v1/blacklist")
    suspend fun setBlackList(
        @Query("firebase_uid") uid: String,
        @Query("bl_state") bl: String
    ): Response<RemoteUser>

}
