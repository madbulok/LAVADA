package com.uzlov.dating.lavada.data.repository

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.data.data_sources.interfaces.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import okhttp3.RequestBody
import javax.inject.Inject

class UserRemoteRepositoryImpl @Inject constructor(
    private var remoteDataSource: IRemoteDataSource
) :
    IUsersRepository {
    override fun getUsers(): LiveData<List<User>> = remoteDataSource.getUsers()

    override suspend fun getUser(id: String): User? = remoteDataSource.getUser(id)

    override fun removeUser(id: String) = remoteDataSource.removeUser(id)

    override fun putUser(user: User) = remoteDataSource.putUser(user)
    override fun updateUser(id: String, field: String, value: Any) =
        remoteDataSource.updateUser(id, field, value)

    override fun observeMatches(uid: String, matchesCallback: MatchesService.MatchesStateListener)
            = remoteDataSource.observeMatches(uid, matchesCallback)

    override suspend fun getRemoteUser(token: String): User? = remoteDataSource.getRemoteUser(token)
    override suspend fun getRemoteUserById(token: String, id: String) = remoteDataSource.getRemoteUserById(token, id)

    override suspend fun authRemoteUser(token: HashMap<String?, String?>): String? = remoteDataSource.authUser(token)
    override suspend fun getRemoteUsers(token: String) = remoteDataSource.getRemoteUsers(token)
    override suspend fun getUserBalance(token: String) = remoteDataSource.getUserBalance(token)
    override suspend fun updateRemoteUser(token: String, field: Map<String, String>) = remoteDataSource.updateRemoteUser(token, field)
    override suspend fun updateRemoteData(token: String, field: HashMap<String, RequestBody>) = remoteDataSource.updateRemoteData(token, field)

    override suspend fun postBalance(token: String, balance: Map<String, String>) = remoteDataSource.postBalance(token, balance)
    override suspend fun postSubscribe(token: String, subscribe: Map<String, String>) = remoteDataSource.postSubscribe(token, subscribe)
    override suspend fun setLike(token: String, requestBody: RequestBody) = remoteDataSource.setLike(token, requestBody)
    override suspend fun checkLike(token: String, firebaseUid: String) = remoteDataSource.checkLike(token, firebaseUid)

}