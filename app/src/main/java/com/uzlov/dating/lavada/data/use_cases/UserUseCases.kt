package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.retrofit.IServerDataSource
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import javax.inject.Inject

class UserUseCases @Inject constructor(
    private var userRemoteDataSource: IServerDataSource<Any>
) {
    suspend fun getUsers(token: String) = userRemoteDataSource.getUsers(token)
    suspend fun getUser(id: String) = userRemoteDataSource.getUser(id)
    suspend fun updateUser(token: String, field: Map<String, String>) = userRemoteDataSource.updateUser(token, field)
    suspend fun saveUser(token: String, user: User) = userRemoteDataSource.saveUser(token, user)
    suspend fun removeUser(token: String, id: String) = userRemoteDataSource.removeUser(token, id)

    /**пользователи*/
    suspend fun getRemoteUser(token:String) = userRemoteDataSource.getUser(token)
    suspend fun getRemoteUserById(token:String, id: String) = userRemoteDataSource.getUserById(token, id)
    suspend fun getRemoteUsers(token:String) = userRemoteDataSource.getUsers(token)
    suspend fun authRemoteUser(token: HashMap<String, String?>) = userRemoteDataSource.authUser(token)
    suspend fun getUserBalance(token: String) = userRemoteDataSource.getBalance(token)
    suspend fun updateRemoteUser(token: String, field: Map<String, String>) = userRemoteDataSource.updateUser(token, field)
    suspend fun updateRemoteData(token: String, field: MultipartBody.Part)=userRemoteDataSource.updateData(token, field)
    suspend fun postBalance(token: String, balance: Map<String, String>) = userRemoteDataSource.postBalance(token, balance)

    suspend fun postSubscribe(token: String, subscribe: Map<String, String>) = userRemoteDataSource.postSubscribe(token, subscribe)
    /**лайки*/
    suspend fun setLike(token: String, requestBody: RequestBody) = userRemoteDataSource.setLike(token, requestBody)
    suspend fun checkLike(token: String, firebaseUid: String) = userRemoteDataSource.checkLike(token, firebaseUid)

    fun setToken(token: String){
        userRemoteDataSource.setToken(token)
    }
}