package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.data.data_sources.ILocalUserDataSource
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserLocalRepository : ILocalUserDataSource {
    override fun getUsers(): Flow<User> {
        return flowOf(User("", MALE.MAN, 12, "", "", ""))
    }

    override fun putUsers(user: User) {

    }

    override fun clear() {
        // clear local data
    }
}