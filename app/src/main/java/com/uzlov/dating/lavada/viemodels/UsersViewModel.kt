package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.ViewModel
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.domain.logic.distance
import com.uzlov.dating.lavada.domain.models.User
import javax.inject.Inject

class UsersViewModel @Inject constructor(private var usersUseCases: UserUseCases?) : ViewModel() {

    fun getUsers() = usersUseCases?.getUsers()

    fun getUser(uid: String) = usersUseCases?.getUser(uid)

    fun addUser(user: User) = usersUseCases?.putUser(user)

    fun removeUser(id: String) = usersUseCases?.removeUsers(id)

    fun sortUsers(
        data: List<User>,
        myLat: Double,
        myLon: Double,
        prefMALE: Int,
        prefAgeStart: Int,
        prefAgeEnd: Int
    ): List<User> {
        val myData = data.toMutableList()
        val iterator = myData.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            val itemLat = item.lat!!
            val itemLon = item.lon!!
            item.dist = distance(myLat, myLon, itemLat, itemLon)
            if (item.dist == 0.0
                || item.male!!.ordinal != prefMALE
                || item.age!! !in prefAgeStart..prefAgeEnd
            ) {
                iterator.remove()
            }
        }
        return myData.sortedBy { it.dist }
    }



}