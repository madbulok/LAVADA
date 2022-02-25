package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.flow.Flow

interface ILocalUserDataSource {
    fun getUsers() : Flow<User>
    fun putUsers(user: User)
    fun clear()
}