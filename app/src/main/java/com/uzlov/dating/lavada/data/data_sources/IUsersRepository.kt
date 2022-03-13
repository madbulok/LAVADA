package com.uzlov.dating.lavada.data.data_sources

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.domain.models.User

interface IUsersRepository {
    fun getUsers(): LiveData<List<User>>
    suspend fun getUser(id: String): User?
    fun removeUser(id: String)
    fun putUser(user: User)
    fun updateUser(id: String, field: String, value: Any)

}