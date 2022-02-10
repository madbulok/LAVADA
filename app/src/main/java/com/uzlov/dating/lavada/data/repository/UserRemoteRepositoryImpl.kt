package com.uzlov.dating.lavada.data.repository

import androidx.lifecycle.LiveData
import com.uzlov.dating.lavada.data.data_sources.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.data.data_sources.IUsersRepository
import javax.inject.Inject

class UserRemoteRepositoryImpl @Inject constructor(var remoteDataSource: IRemoteDataSource
    ) :
    IUsersRepository {
    override fun getUsers(): LiveData<List<User>> = remoteDataSource.getUsers()

    override fun getUser(id: String): LiveData<User?> = remoteDataSource.getUser(id)
    override fun getUsersWithUserID(id: String): LiveData<List<User>> =
        remoteDataSource.getUsersWithUserID(id)

    override fun removeUser(id: String) = remoteDataSource.removeUser(id)

    override fun putUser(request: User) = remoteDataSource.putUser(request)

}