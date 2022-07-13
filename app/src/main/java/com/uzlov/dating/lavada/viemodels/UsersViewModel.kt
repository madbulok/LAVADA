package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.auth.FirebaseEmailAuthService
import com.uzlov.dating.lavada.data.convertDtoToModel
import com.uzlov.dating.lavada.data.convertListDtoToModel
import com.uzlov.dating.lavada.data.convertToLike
import com.uzlov.dating.lavada.data.displayApiResponseErrorBody
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
import kotlin.collections.set


class UsersViewModel @Inject constructor(
    private val usersUseCases: UserUseCases,
    private val authService: FirebaseEmailAuthService,
    private val serverCommunication: ServerCommunication
) : ViewModel() {

    private val selfUser = MutableLiveData<User>()
    private val userById = MutableLiveData<User>()
    private val userByLavadaId = MutableLiveData<User>()
    private var tokenResult = MutableLiveData<String>()
    private var listUsers = MutableLiveData<List<User>>()
    private var response = MutableLiveData<RemoteUser>()
    private var uploadedFile = MutableLiveData<RemoteUser>()
    private var selfBalance = MutableLiveData<Int>()
    private var updatedBalance = MutableLiveData<User>()
    private var updatedLike = MutableLiveData<User>()
    private var checkedLike = MutableLiveData<RemoteUser>()
    private var userBI = MutableLiveData<List<User>>()
    val likes get() : LiveData<User> = updatedLike
    val listUsersData get() : LiveData<List<User>> = listUsers
    val selfUserData get() : MutableLiveData<User> = selfUser
    val status = MutableLiveData<String?>()
    val updatedUserData get() : MutableLiveData<RemoteUser> = response
    val uploadedFileData get() : MutableLiveData<RemoteUser> = uploadedFile
    val selfBalanceData get() : MutableLiveData<Int> = selfBalance
    val updatedBalanceData get() : MutableLiveData<User> = updatedBalance
    val checkedLikeData get() : MutableLiveData<RemoteUser> = checkedLike
    val userByIdData get() : MutableLiveData<User> = userById
    val tokenResultData get(): MutableLiveData<String> = tokenResult
    val userBIData get(): MutableLiveData<List<User>> = userBI
    val userByLavadaIdData get(): MutableLiveData<User> = userByLavadaId


    /**
     * Получаем пользователя с сервера
     * @param token fb
     */
    fun getUser(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.getUserAsync()?.let { reUser ->
                        if (reUser.isSuccessful) {
                            selfUser.postValue(
                                reUser.body()?.let { it1 -> convertDtoToModel(it1) })
                        } else {
                            status.postValue(displayApiResponseErrorBody(reUser))
                        }
                    }
                }
            }
        }
    }

    /**
     * Получает пользователя по UID
     * @param token fb
     * @param uid идентификатор пользователя на fb
     */
    fun getUserById(token: String, uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser = serverCommunication.apiServiceWithToken?.getUserByIdAsync(uid)
                    if (reUser!!.isSuccessful && reUser.body() != null) {
                        userById.postValue(convertDtoToModel(reUser.body()!!))
                    } else {
                        status.postValue(displayApiResponseErrorBody(reUser))
                    }
                }
            }
        }
    }

    fun getUserByLavadaId(token: String, uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser = serverCommunication.apiServiceWithToken?.getUserByLavadaIdAsync(uid)
                    if (reUser!!.isSuccessful && reUser.body() != null) {
                        userByLavadaId.postValue(convertDtoToModel(reUser.body()!!))
                    } else {
                        status.postValue(displayApiResponseErrorBody(reUser))
                    }
                }
            }
        }
    }

    /**
     * Отправляет пользователя на сервер
     * @param token fb
     * @param field<String, String> - поле, которое нужно обновить
     */
    fun updateUser(token: String, field: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser = serverCommunication.apiServiceWithToken?.updateUserAsync(field)!!
                    if (reUser.isSuccessful) {
                        response.postValue(
                            reUser.body()
                        )
                    } else {
                        status.postValue(displayApiResponseErrorBody(reUser))
                    }
                }
            }
        }
    }

    /**
     * Отправляет файл на сервер
     * @param token fb
     * @param field<MultipartBody.Part> - поле, которое нужно обновить. Принимает в себя файл
     */
    fun updateRemoteData(token: String, field: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser = serverCommunication.apiServiceWithToken?.uploadEmployeeDataAsync(
                        field
                    )!!
                    if (reUser.isSuccessful) {
                        uploadedFile.postValue(
                            reUser.body()
                        )
                    } else {
                        status.postValue(displayApiResponseErrorBody(reUser))
                    }
                }
            }
        }
    }

    /**
     * Отправляет пользователя на сервер
     * @param token пользователя с бэка
     * @param user - объект пользователя
     * это нам вообще надо?
     */
    fun saveUser(token: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.saveUser(token, user)
        }
    }

    /**
     * Получаем список пользователей с сервера
     * @param token fb
     */
    fun getUsers(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // запускаем наш токен
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                // получаем токен с бэка
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    // прописываем его в запросы
                    serverCommunication.updateToken(tokenToo)
                    serverCommunication.apiServiceWithToken?.getUsersAsync("200")?.let { result ->
                        Log.e("GET_USERS", result.body().toString())
                        val listUser = mutableListOf<User>()
                        if (result.isSuccessful) {
                            val list = result.body()?.data?.rows
                            if (list != null) {
                                for (reUser in list) {
                                    listUser.add(convertListDtoToModel(reUser!!))
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
                        } else {
                            status.postValue(displayApiResponseErrorBody(result))
                        }

                    }
                }
            }
        }
    }


    /**
     * Получаем баланс пользователя
     * @param token fb
     */
    fun getRemoteBalance(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser = serverCommunication.apiServiceWithToken?.getUserBalanceAsync()
                    reUser.let { response ->
                        if (response!!.isSuccessful) {
                            val resBal = response.body()?.data?.user_balance?.toDouble()?.toInt()
                            selfBalance.postValue(resBal!!)
                        } else {
                            status.postValue(displayApiResponseErrorBody(response))
                        }
                    }
                }
            }
        }
    }

    /**
     * Обновляем баланс пользователя
     * @param token fb
     * @param balance String - сумма пополнения
     * какая-то херота с этим балансом творится. Подозреваю бэк
     */
    fun postRemoteBalance(token: String, balance: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val body = mutableMapOf<String, String>()
                    body["pay_system"] = "google"
                    body["trx_id"] = UUID.randomUUID().toString()
                    body["amount"] = balance
                    body["meta"] = "{}"
                    body["status"] = "succeeded"
                    val result = serverCommunication.apiServiceWithToken?.postBalanceAsync(body)!!
                    result.let { response ->
                        if (!response.isSuccessful) {
                            status.postValue(displayApiResponseErrorBody(result))
                        }
                    }
                }
            }
        }
    }

    /**
     * Авторизация пользователя на сервере
     * @param token токен после авторизации или регистрации пользователя через FirebaseAuth
     * */
    fun authRemoteUser(token: HashMap<String, String?>) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(token).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(token)
                if (result!!.isSuccessful) {
                    Log.e("AUTH_REMOTE_USER", result.body()?.data?.token.toString())
                    tokenResult.postValue(result.body()?.data?.token.toString())
                } else {
                    status.postValue(displayApiResponseErrorBody(result))
                }

            }
        }
    }

    /**
     * Обновляем подписку пользователя
     * @param token fb
     * @param subscribe : String, вида "2022-05-10 00:00:00"
     * @param amount : String - сумма оплаты
     */
    fun postSubscribe(
        token: String,
        subscribe: String,
        amount: String
    ){
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val body = mutableMapOf<String, String>()
                    body["expiration_pay"] = subscribe
                    body["pay_system"] = "google"
                    body["trx_id"] = UUID.randomUUID().toString()
                    body["amount"] = amount
                    body["meta"] = "{}"
                    body["status"] = "succeeded"
                    val response =
                        serverCommunication.apiServiceWithToken?.postSubscribeAsync(body)
                    if (!response!!.isSuccessful) {
                        status.postValue(displayApiResponseErrorBody(response))
                    }
                }
            }
        }
    }


    /**
     * Ставим лайк
     * @param tokenFB пользователя с firebase
     * @param firebaseUid : String, uid пользователя, которому ставим лайк
     * @param likeState : String; "1" - поставить лайк, "0" - снять лайк
     */
    fun setLike(tokenFB: String, firebaseUid: String, likeState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // запускаем наш токен
            usersUseCases.authRemoteUser(hashMapOf("token" to tokenFB)).let {
                // получаем токен с бэка
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to tokenFB))
                result?.body()?.data?.token?.let { tokenToo ->
                    // прописываем его в запросы
                    serverCommunication.updateToken(tokenToo)
                    val response =
                        serverCommunication.apiServiceWithToken?.setLike(
                            firebaseUid,
                            likeState
                        )
                    if (response!!.isSuccessful) {
                        response.body()?.let { reUser ->
                            val like = convertToLike(reUser.data)
                            val user = convertDtoToModel(
                                serverCommunication.apiServiceWithToken?.getUserByIdAsync(
                                    firebaseUid
                                )!!.body()!!
                            )
                            val matches = mutableMapOf<String, Boolean>()
                            matches[firebaseUid] = like
                            user.matches = matches
                            updatedLike.postValue(user)
                        }
                    } else {
                        status.postValue(displayApiResponseErrorBody(response))
                    }
                }
            }
        }
    }

    /**
     * Проверяем лайк на взаимность
     * @param token fb
     * @param firebaseUid : String, uid пользователя, которому ставим лайк
     */
    fun checkLike(token: String, firebaseUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usersUseCases.authRemoteUser(hashMapOf("token" to token)).let {
                val result =
                    serverCommunication.apiServiceWithToken?.authUserAsync(hashMapOf("token" to token))
                result?.body()?.data?.token?.let { tokenToo ->
                    serverCommunication.updateToken(tokenToo)
                    val reUser =
                        serverCommunication.apiServiceWithToken?.checkLikeAsync(firebaseUid)
                    if (reUser!!.isSuccessful) {
                        checkedLike.postValue(reUser.body())
                    } else {
                        status.postValue(displayApiResponseErrorBody(reUser))
                    }
                }
            }
        }
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