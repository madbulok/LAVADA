package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.*
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.logic.distance
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    private val usersUseCases: UserUseCases,
    private val authService: FirebaseEmailAuthService,
    private val serverCommunication: ServerCommunication
) : ViewModel() {

    private val selfUser = MutableLiveData<User>()
    private val userById = MutableLiveData<User>()
    private val remoteUserById = MutableLiveData<User>()
    private val tokenResult = MutableLiveData<String>()

    /**
     * Получаем пользователя с сервера
     * @param token собственный токен
     * */
    fun getUser() : LiveData<User?>{

        authService.getUser()?.getIdToken(true)?.addOnSuccessListener {
            usersUseCases.setToken(it.token!!)
                viewModelScope.launch(Dispatchers.IO) {
                    usersUseCases.getRemoteUser(it.token!!)
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
            usersUseCases.getUser(uid).let {

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
//            usersUseCases.saveUser(user)
        }
    }

    /**
     * Получает пользователя на сервер
     * @param token ваш токен
     * @param id uid пользователя
     * */
    fun getRemoteUserById(token: String, id: String) : LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.getRemoteUserById(token, id).let {

            }
        }
        return remoteUserById
    }

    private val listUsers = MutableLiveData<List<Any>>()

    //получаем список пользователей с fb
    fun getUsers(token: String): LiveData<List<Any>> {
            viewModelScope.launch(Dispatchers.IO) {
                serverCommunication.updateToken(token) // обновляем токен полученый с сервера
                val result = serverCommunication.apiServiceWithToken?.getUsersAsync()
                val users = result?.await()
                Log.e("TAG", "getUsers: ${users}")
//                listUsers.postValue(usersUseCases.getUsers(it.token ?: ""))
            }


        return listUsers
    }



    /**
     * Авторизация пользователя на сервере
     * @param token токен после авторизации или регистрации пользователя через FirebaseAuth
    * */
    fun authRemoteUser(token: HashMap<String, String?>) : LiveData<String>{
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(token).let {

            }
        }
        return tokenResult
    }

    // пользователи
    suspend fun getRemoteUsers(token: String) = usersUseCases.getRemoteUsers(token)
    suspend fun getRemoteBalance(token: String) = usersUseCases.getUserBalance(token)
    suspend fun postRemoteBalance(token: String, balance: Map<String, String>) = usersUseCases.postBalance(token, balance)
    suspend fun postSubscribe(token: String, subscribe: Map<String, String>) = usersUseCases.postSubscribe(token, subscribe)
    suspend fun updateRemoteUser(token: String, field: HashMap<String, String>) = usersUseCases.updateRemoteUser(token, field)
    suspend fun updateRemoteData(token: String, field: HashMap<String, RequestBody>) = usersUseCases.updateRemoteData(token, field)

    //лайки
    suspend fun setLike(token: String, requestBody: RequestBody) = usersUseCases.setLike(token, requestBody)
    suspend fun checkLike(token: String, firebaseUid: String) = usersUseCases.checkLike(token, firebaseUid)



    fun removeUser(token: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.removeUser(token, id)
        }
    }

    fun updateUser(token: String, field: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.updateUser(token, field)
        }
    }


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

    override fun onCleared() {
        super.onCleared()
        serverCommunication.destroy() // освобождаем память
    }
}