package com.uzlov.dating.lavada.data.data_sources

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import okhttp3.RequestBody

interface IUsersRepository {
    //получить список всех пользователей
    fun getUsers(): LiveData<List<User>>

    //получить пользователя по id
    suspend fun getUser(id: String): User?

    //удалить пользователя
    fun removeUser(id: String)

    //добавить пользователя
    fun putUser(user: User)

    //обновить данные о пользователе
    fun updateUser(id: String, field: String, value: Any)

    //получить инфу о лайках (новый/взаимно или нет)
    fun observeMatches(
        uid: String,
        matchesCallback: MatchesService.MatchesStateListener,
    )

    /* методы для нового бэка*/
    /**пользователи*/
    suspend fun getRemoteUser(token: String): User?
    suspend fun getRemoteUserById(token: String, id: String): User?
    suspend fun authRemoteUser(token: HashMap<String?, String?>): String?
    suspend fun getRemoteUsers(token: String)
    suspend fun getUserBalance(token: String)
    suspend fun updateRemoteUser(token: String, field: Map<String, String>)
    suspend fun updateRemoteData(token: String, field: HashMap<String, RequestBody>)
    suspend fun postBalance(token: String, balance: Map<String, String>)
    suspend fun postSubscribe(token: String, subscribe: Map<String, String>)

    /**лайки*/
    suspend fun setLike(token: String, requestBody: RequestBody)
    suspend fun checkLike(token: String, firebaseUid: String)
}