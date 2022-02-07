package com.uzlov.dating.lavada.data.data_sources

import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.flow.Flow

interface  IRemoteDataSource {

    fun getUsers() : Flow<User>
}