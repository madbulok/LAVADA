package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.interfaces.ILocalUserDataSource
import com.uzlov.dating.lavada.data.data_sources.interfaces.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User
import javax.inject.Inject

class UserUseCases @Inject constructor(
    private val localRepository: ILocalUserDataSource,
    private val remoteRepository: IRemoteDataSource,
) {
    fun getUsers() = remoteRepository.getUsers()
    fun getUsers(id: String) = remoteRepository.getUser(id)
    fun removeUsers(id: String) = remoteRepository.removeUser(id)
    fun putUser(user: User) = remoteRepository.putUser(user)

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