package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.*
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.domain.logic.distance
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.service.MatchesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

class UsersViewModel @Inject constructor(private var usersUseCases: UserUseCases?) : ViewModel() {

    private val selfUser = MutableLiveData<User>()
    private val userById = MutableLiveData<User>()
    private val remoteUserById = MutableLiveData<User>()

    /**
     * Получаем пользователя с сервера
     * @param token собственный токен
     * */
    fun getRemoteUser(token: String) : LiveData<User?>{
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases?.getRemoteUser(token)?.let {
                selfUser.postValue(it)
            }
        }
        return selfUser
    }

    /**
     * Получает пользователя по UID
     * @param uid идентификатор пользователя
     * */
    fun getUser(uid: String): LiveData<User?> {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases?.getUser(uid)?.let {
                userById.postValue(it)
            }
        }
        return userById
    }

    /**
     * Отправляет пользователя на сервер
     * @param user объект пользователя
     * */
    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases?.putUser(user)
        }
    }

    /**
     * Отправляет пользователя на сервер
     * @param token ваш токен
     * @param id uid пользователя
     * */
    fun getRemoteUserById(token: String, id: String) : LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases?.getRemoteUserById(token, id)?.let {
                remoteUserById.postValue(it)
            }
        }
        return remoteUserById
    }


    //получаем список пользователей с fb
    fun getUsers() = usersUseCases?.getUsers()



    //авторизуем юзера
    suspend fun authRemoteUser(token: HashMap<String?, String?>) = usersUseCases?.authRemoteUser(token)

    // пользователи
    suspend fun getRemoteUsers(token: String) = usersUseCases?.getRemoteUsers(token)
    suspend fun getRemoteBalance(token: String) = usersUseCases?.getUserBalance(token)
    suspend fun postRemoteBalance(token: String, balance: Map<String, String>) = usersUseCases?.postBalance(token, balance)
    suspend fun postSubscribe(token: String, subscribe: Map<String, String>) = usersUseCases?.postSubscribe(token, subscribe)
    suspend fun updateRemoteUser(token: String, field: HashMap<String, String>) = usersUseCases?.updateRemoteUser(token, field)
    suspend fun updateRemoteData(token: String, field: HashMap<String, RequestBody>) = usersUseCases?.updateRemoteData(token, field)

    //лайки
    suspend fun setLike(token: String, requestBody: RequestBody) = usersUseCases?.setLike(token, requestBody)
    suspend fun checkLike(token: String, firebaseUid: String) = usersUseCases?.checkLike(token, firebaseUid)



    fun removeUser(id: String) = usersUseCases?.removeUsers(id)

    fun updateUser(id: String, field: String, value: Any) =
        usersUseCases?.updateUser(id, field, value)

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
            if (blockedUID.contains(item.uid)) {
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