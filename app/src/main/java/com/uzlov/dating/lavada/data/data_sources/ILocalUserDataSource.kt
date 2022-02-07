package com.uzlov.dating.lavada.data.data_sources

import kotlinx.coroutines.flow.Flow

interface ILocalUserDataSource {
    fun getUsers() : Flow<String>
    fun putUsers(user: String)
    fun clear()
}