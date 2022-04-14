package com.uzlov.dating.lavada.retrofit

import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.firebase.database.core.Repo
import kotlinx.coroutines.Deferred


interface ApiService {

    @GET("/users")
    fun getUsersAsync(): Deferred<List<RemoteUser>>

//    @GET("/users")
//    fun UsersAsync(
//        @Header("token") token: String,
//    ) : Deferred<List<RemoteUser>>

}
