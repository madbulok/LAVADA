package com.uzlov.dating.lavada.data.data_sources

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import kotlinx.coroutines.flow.Flow
import retrofit2.Call

interface IUsersRepository {
    fun getUsers(): LiveData<List<User>>
    suspend fun getUser(id: String): User?
    fun removeUser(id: String)
    fun putUser(user: User)
    fun updateUser(id: String, field: String, value: Any)
    fun observeMatches(
        uid: String,
        matchesCallback: MatchesService.MatchesStateListener,
    )
    suspend fun getRemoteUsers()
}