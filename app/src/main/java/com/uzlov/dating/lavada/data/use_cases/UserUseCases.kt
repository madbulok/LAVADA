package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
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