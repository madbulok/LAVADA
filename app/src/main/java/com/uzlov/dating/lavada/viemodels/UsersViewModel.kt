package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.ViewModel
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.domain.models.User
import javax.inject.Inject

class UsersViewModel @Inject constructor(private var usersUseCases: UserUseCases?)  : ViewModel() {

    fun getUsers() = usersUseCases?.getUsers()

    fun getUser(uid: String) = usersUseCases?.getUser(uid)

    fun addUser(user: User) = usersUseCases?.putUser(user)

    fun removeUser(id: String) = usersUseCases?.removeUsers(id)
}