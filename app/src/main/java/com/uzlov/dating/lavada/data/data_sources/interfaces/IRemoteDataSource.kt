package com.uzlov.dating.lavada.data.data_sources.interfaces

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.flow.Flow

interface  IRemoteDataSource {
    //   fun getUsers() : Flow<User>
    fun getUsers(): LiveData<List<User>>
    fun getUser(id: String): LiveData<User?>
    fun removeUser(id: String)
    fun putUser(user: User)
    fun getUsersWithUserID(id: String): LiveData<List<User>>
    fun updateUser(id: String, field: String, value: Any)
}