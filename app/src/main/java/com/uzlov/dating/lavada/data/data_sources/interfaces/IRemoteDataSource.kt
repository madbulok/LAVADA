package com.uzlov.dating.lavada.data.data_sources.interfaces

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import okhttp3.RequestBody

interface  IRemoteDataSource {

    fun getUsers(): LiveData<List<User>>
    suspend fun getUser(id: String): User?
    fun removeUser(id: String)
    fun putUser(user: User)
    fun getUsersWithUserID(id: String): LiveData<List<User>>
    fun updateUser(id: String, field: String, value: Any)
    fun observeMatches(
        uid: String,
        matchesCallback: MatchesService.MatchesStateListener
    )
    /**пользователи*/
    suspend fun getRemoteUser(token: String): User?
    suspend fun getRemoteUserById(token: String, id: String): User?
    suspend fun authUser(token: HashMap<String?, String?>): String?
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