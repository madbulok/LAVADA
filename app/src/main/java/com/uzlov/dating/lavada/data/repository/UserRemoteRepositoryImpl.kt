package com.uzlov.dating.lavada.data.repository

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.data.data_sources.interfaces.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import javax.inject.Inject

class UserRemoteRepositoryImpl @Inject constructor(
    private var remoteDataSource: IRemoteDataSource
) :
    IUsersRepository {
    override fun getUsers(): LiveData<List<User>> = remoteDataSource.getUsers()

    override suspend fun getUser(id: String): User? = remoteDataSource.getUser(id)

    override fun removeUser(id: String) = remoteDataSource.removeUser(id)

    override fun putUser(user: User) = remoteDataSource.putUser(user)
    override fun updateUser(id: String, field: String, value: Any) =
        remoteDataSource.updateUser(id, field, value)


}