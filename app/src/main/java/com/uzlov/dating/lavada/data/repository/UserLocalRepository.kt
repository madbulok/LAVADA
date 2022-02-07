package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.data.data_sources.ILocalUserDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserLocalRepository : ILocalUserDataSource {
    override fun getUsers(): Flow<String> {
        return flowOf("1", "2", "3")
    }

    override fun putUsers(user: String) {

    }

    override fun clear() {
        // clear local data
    }
}