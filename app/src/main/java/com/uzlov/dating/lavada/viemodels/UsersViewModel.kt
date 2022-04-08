package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.domain.logic.distance
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsersViewModel @Inject constructor(private var usersUseCases: UserUseCases?) : ViewModel() {

    fun getUsers() = usersUseCases?.getUsers()

    fun getUser(uid: String): LiveData<User?>{
       return liveData {
           viewModelScope.launch(Dispatchers.IO) {
               emit(usersUseCases?.getUser(uid))
           }
       }
    }

    suspend fun getUserSuspend(uid: String) : User? {
        return usersUseCases?.getUser(uid)
    }

    fun addUser(user: User) = usersUseCases?.putUser(user)

    fun removeUser(id: String) = usersUseCases?.removeUsers(id)

    fun updateUser(id: String, field: String, value: Any) = usersUseCases?.updateUser(id, field, value)

    fun sortUsers(
        data: List<User>,
        myLat: Double,
        myLon: Double,
        prefMALE: Int,
        prefAgeStart: Int,
        prefAgeEnd: Int,
        blockedUID: List<String>
    ): List<User> {
        val myData = data.toMutableList()
        val iterator = myData.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            val itemLat = item.lat!!
            val itemLon = item.lon!!
            item.dist = distance(myLat, myLon, itemLat, itemLon)
            if (item.dist == 0.0 || item.dist!!.isNaN()
                || item.male!!.ordinal != prefMALE
                || item.age!! !in prefAgeStart..prefAgeEnd
                || item.url_video!!.isNullOrEmpty() || blockedUID.contains(item.uid)
            ) {
                iterator.remove()
            }
        }
        return myData.sortedBy { it.dist }
    }

    fun blockedUsers(
        data: List<User>,
        blockedUID: List<String>
    ): List<User> {
        val myBlackList = mutableListOf<User>()
        val myData = data.toMutableList()
        val iterator = myData.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (blockedUID.contains(item.uid)){
                myBlackList.add(item)
            }
        }
        return myBlackList
    }

    fun observeMatches(
        phone: String,
        matchesCallback: MatchesService.MatchesStateListener,
    ) = usersUseCases?.observeMatches(phone, matchesCallback)
}