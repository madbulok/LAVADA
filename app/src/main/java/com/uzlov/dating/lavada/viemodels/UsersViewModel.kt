package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.*
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.convertDtoToModel
import com.uzlov.dating.lavada.data.convertListDtoToModel
import com.uzlov.dating.lavada.data.use_cases.UserUseCases
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.logic.distance
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    private val usersUseCases: UserUseCases,
    private val authService: FirebaseEmailAuthService,
    private val serverCommunication: ServerCommunication
) : ViewModel() {

    private val selfUser = MutableLiveData<User>()
    private val userById = MutableLiveData<User>()
    private var tokenResult = MutableLiveData<String>()
    private var listUsers = MutableLiveData<List<User>>()
    private var response = Any()
    private var user = RemoteUser()
    private var selfBalance = MutableLiveData<Int>()
    private var updatedBalance = MutableLiveData<User>()


    /**
     * Получаем пользователя с сервера
     * @param token собственный токен, отданный после аутентификации с бэка
     */
    fun getUser(token: String): LiveData<User?> {
        authService.getUser()?.getIdToken(true)?.addOnSuccessListener {
            viewModelScope.launch(Dispatchers.IO) {
                serverCommunication.updateToken(token) // обновляем токен полученый с сервера
                val reUser = serverCommunication.apiServiceWithToken?.getUserAsync()?.await()
                if (reUser != null) {
                    selfUser.postValue(convertDtoToModel(reUser))
                }
            }
        }
        return selfUser
    }

    /**
     * Получает пользователя по UID
     * @param token пользователя с бэка
     * @param uid идентификатор пользователя на fb
     */
    fun getUserById(token: String, uid: String): LiveData<User?> {
        authService.getUser()?.getIdToken(true)?.addOnSuccessListener {
            viewModelScope.launch(Dispatchers.IO) {
                serverCommunication.updateToken(token) // обновляем токен полученый с сервера
                val reUser = serverCommunication.apiServiceWithToken?.getUserByIdAsync(uid)?.await()
                if (reUser != null) {
                    userById.postValue(convertDtoToModel(reUser))
                }
            }
        }
        return userById
    }

    /**
     * Отправляет пользователя на сервер
     * @param token пользователя с бэка
     * @param field<String, String> - поле, которое нужно обновить
     */
    fun updateUser(token: String, field: Map<String, String>): Any {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            usersUseCases.updateUser(token, field)
            response = serverCommunication.apiServiceWithToken?.updateUserAsync(field)!!
        }
        return response
    }

    /**
     * Отправляет файл на сервер
     * @param token пользователя с бэка
     * @param field<MultipartBody.Part> - поле, которое нужно обновить. Принимает в себя файл
     */
    fun updateRemoteData(token: String, field: MultipartBody.Part): RemoteUser? {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            user = serverCommunication.apiServiceWithToken?.uploadEmployeeDataAsync(field)!!.await()
        }
        return user
    }

    /**
     * Отправляет пользователя на сервер
     * @param token пользователя с бэка
     * @param user - объект пользователя
     */
    fun saveUser(token: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.saveUser(token, user)
        }
    }

    /**
     * Получаем список пользователей с сервера
     * @param token пользователя с бэка
     */
    fun getUsers(token: String): LiveData<List<User>> {
        authService.getUser()?.getIdToken(true)?.addOnSuccessListener {
            viewModelScope.launch(Dispatchers.IO) {
                serverCommunication.updateToken(token) // обновляем токен полученый с сервера
                val result = serverCommunication.apiServiceWithToken?.getUsersAsync()?.await()
                val listUser = mutableListOf<User>()
                if (result != null) {
                    val ir = result.data?.rows
                    if (ir != null) {
                        for (i in ir) {
                            listUser.add(convertListDtoToModel(i!!))
                        }
                    }
                }
                val iterator = listUser.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    Log.e("ITERATOR", item.url_video.toString())
                    if (item.url_video.isNullOrEmpty()) {
                        iterator.remove()
                    }
                }
                listUsers.postValue(listUser)
            }
        }
        return listUsers
    }

    /**
     * Получаем баланс пользователя
     * @param token пользователя с бэка
     */
    fun getRemoteBalance(token: String): LiveData<Int> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            val result = serverCommunication.apiServiceWithToken?.getUserBalanceAsync()?.await()
            val resBal = result?.data?.user_balance?.toDouble()?.toInt()
            selfBalance.postValue(resBal!!)
        }
        return selfBalance
    }

    /**
     * Обновляем баланс пользователя
     * @param token пользователя с бэка
     * @param balance Map<String, String>, вида: balance[""] = "1000"
     * какая-то херота с этим балансом творится. Подозреваю бэк
     */
    fun postRemoteBalance(token: String, balance: String): LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            val body = mutableMapOf<String, String>()
            body["pay_system"] = "google"
            body["trx_id"] = UUID.randomUUID().toString()
            body["amount"] = balance
            body["meta"] = "{}"
            body["status"] = "succeeded"
          serverCommunication.apiServiceWithToken?.postBalanceAsync(body)?.await()
        }
        return updatedBalance
    }

    /**
     * Авторизация пользователя на сервере
     * @param token токен после авторизации или регистрации пользователя через FirebaseAuth
     * */
    fun authRemoteUser(token: HashMap<String, String?>): LiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(token).let {
                val result = serverCommunication.apiServiceWithToken?.authUserAsync(token)
                val tokenToo = result?.await()
                Log.e("AUTH_REMOTE_USER", tokenToo?.data?.token.toString())
                tokenResult.postValue(tokenToo?.data?.token.toString())
            }
        }
        return tokenResult
    }

    // пользователи


    suspend fun postSubscribe(token: String, subscribe: Map<String, String>) =
        usersUseCases.postSubscribe(token, subscribe)


    //лайки
    suspend fun setLike(token: String, requestBody: RequestBody) =
        usersUseCases.setLike(token, requestBody)

    suspend fun checkLike(token: String, firebaseUid: String) =
        usersUseCases.checkLike(token, firebaseUid)


    fun removeUser(token: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.removeUser(token, id)
        }
    }

    //сортировку переписать
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