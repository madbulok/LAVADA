package com.uzlov.dating.lavada.data.data_sources

import kotlinx.coroutines.flow.Flow

interface  IRemoteDataSource {

    fun getUsers() : Flow<String>
}