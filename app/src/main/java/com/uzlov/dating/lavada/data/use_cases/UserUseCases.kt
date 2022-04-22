package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import okhttp3.RequestBody
import javax.inject.Inject

class UserUseCases @Inject constructor(
    private var userRepository: IUsersRepository
) {
    fun getUsers() = userRepository.getUsers()
    suspend fun getUser(id: String) = userRepository.getUser(id)
    fun removeUsers(id: String) = userRepository.removeUser(id)
    fun putUser(user: User) = userRepository.putUser(user)
    fun updateUser(id: String, field: String, value: Any) = userRepository.updateUser(id, field, value)
    fun observeMatches(
        uid: String,
        matchesCallback: MatchesService.MatchesStateListener,
    ) = userRepository.observeMatches(uid, matchesCallback)

    /**пользователи*/
    suspend fun getRemoteUser(token:String) = userRepository.getRemoteUser(token)
    suspend fun getRemoteUserById(token:String, id: String) = userRepository.getRemoteUserById(token, id)
    suspend fun getRemoteUsers(token:String) = userRepository.getRemoteUsers(token)
    suspend fun authRemoteUser(token: HashMap<String?, String?>) = userRepository.authRemoteUser(token)
    suspend fun getUserBalance(token: String) = userRepository.getUserBalance(token)
    suspend fun updateRemoteUser(token: String, field: Map<String, String>) = userRepository.updateRemoteUser(token, field)
    suspend fun updateRemoteData(token: String, field: HashMap<String, RequestBody>)=userRepository.updateRemoteData(token, field)
    suspend fun postBalance(token: String, balance: Map<String, String>) = userRepository.postBalance(token, balance)
    suspend fun postSubscribe(token: String, subscribe: Map<String, String>) = userRepository.postSubscribe(token, subscribe)
    /**лайки*/
    suspend fun setLike(token: String, requestBody: RequestBody) = userRepository.setLike(token, requestBody)
    suspend fun checkLike(token: String, firebaseUid: String) = userRepository.checkLike(token, firebaseUid)


//    suspend fun getUsers(): Flow<User> {
//        return flow {
//            localRepository.clear()
//            remoteRepository.getUsers().collect {
//                localRepository.putUsers(it)
//                emit(it)
//            }
//        }.flowOn(Dispatchers.IO)
//    }
}