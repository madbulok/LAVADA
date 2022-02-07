package com.uzlov.dating.lavada.data.use_cases

import com.uzlov.dating.lavada.data.data_sources.ILocalUserDataSource
import com.uzlov.dating.lavada.data.data_sources.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserUseCases @Inject constructor(
    private val localRepository: ILocalUserDataSource,
    private val remoteRepository: IRemoteDataSource,
) {

    suspend fun getUsers(): Flow<User> {
        return flow {
            localRepository.clear()
            remoteRepository.getUsers().collect {
                localRepository.putUsers(it)
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}