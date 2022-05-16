package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sortedBy
import kotlin.collections.toMutableList

class UsersViewModel @Inject constructor(
    private val usersUseCases: UserUseCases,
    private val authService: FirebaseEmailAuthService,
    private val serverCommunication: ServerCommunication
) : ViewModel() {

    private val selfUser = MutableLiveData<User>()
    private val userById = MutableLiveData<User>()
    private var tokenResult = MutableLiveData<String>()
    private var listUsers = MutableLiveData<List<User>>()
    private var response = MutableLiveData<RemoteUser>()
    private var user = MutableLiveData<RemoteUser>()
    private var selfBalance = MutableLiveData<Int>()
    private var updatedBalance = MutableLiveData<User>()
    private var updatedLike = MutableLiveData<RemoteUser>()


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
    fun updateUser(token: String, field: Map<String, String>): LiveData<RemoteUser?>?{
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            response.postValue(serverCommunication.apiServiceWithToken?.updateUserAsync(field)!!.await())
        }
        return response
    }

    /**
     * Отправляет файл на сервер
     * @param token пользователя с бэка
     * @param field<MultipartBody.Part> - поле, которое нужно обновить. Принимает в себя файл
     */
    fun updateRemoteData(token: String, field: MultipartBody.Part): LiveData<RemoteUser?>? {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            viewModelScope.launch(Dispatchers.IO) {
                user.postValue(serverCommunication.apiServiceWithToken?.uploadEmployeeDataAsync(field)?.await())

            }
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
                    val listUsers = result.data?.rows
                    if (listUsers != null) {
                        for (reUser in listUsers) {
                            listUser.add(convertListDtoToModel(reUser!!))
                        }
                    }
                }
                val iterator = listUser.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
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
     * @param balance String - сумма пополнения
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

    /**
     * Обновляем подписку пользователя
     * @param token пользователя с бэка
     * @param subscribe : String, вида "2022-05-10 00:00:00"
     * @param amount : String - сумма оплаты
     */
    fun postSubscribe(token: String, subscribe: String, amount: String): LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            val body = mutableMapOf<String, String>()
            body["expiration_pay"] = subscribe
            body["pay_system"] = "google"
            body["trx_id"] = UUID.randomUUID().toString()
            body["amount"] = amount
            body["meta"] = "{}"
            body["status"] = "succeeded"
            serverCommunication.apiServiceWithToken?.postSubscribeAsync(body)?.await()
        }
        return updatedBalance
    }

    /**
     * Ставим лайк
     * @param token пользователя с бэка
     * @param firebaseUid : String, uid пользователя, которому ставим лайк
     * @param likeState : String; "1" - поставить лайк, "0" - снять лайк
     */
    fun setLike(token: String, firebaseUid: String, likeState: String): LiveData<RemoteUser> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token)
            val body = mutableMapOf<String, String>()
            body["firebase_uid"] = firebaseUid
            body["like_state"] = likeState
          //  val reUser = serverCommunication.apiServiceWithToken?.setLikeAsync(body)?.await()
            val reUser = serverCommunication.apiServiceWithToken?.setLike(firebaseUid, likeState)?.await()
            updatedLike.let {
                it.postValue(reUser)
            }

        }
        return updatedLike
    }

    /**
     * Проверяем лайк на взаимность
     * @param token пользователя с бэка
     * @param firebaseUid : String, uid пользователя, которому ставим лайк
     */
    fun checkLike(token: String, firebaseUid: String): MutableLiveData<RemoteUser> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token)
            val reUser =
                serverCommunication.apiServiceWithToken?.checkLikeAsync(firebaseUid)?.await()
            updatedLike.let {
                it.postValue(reUser)
            }

        }
        return updatedLike
    }


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