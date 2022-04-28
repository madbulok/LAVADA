package com.uzlov.dating.lavada.di.modules

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.uzlov.dating.lavada.app.BASE_URL
import com.uzlov.dating.lavada.data.OAuthInterceptor
import com.uzlov.dating.lavada.retrofit.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerCommunication private constructor(private var token: String? = "") {

    private var coroutineAdapterFactory:CoroutineCallAdapterFactory? = CoroutineCallAdapterFactory()
    private var httpClientBuilder: OkHttpClient.Builder? = null
    private var gsonConverterFactory: GsonConverterFactory? = GsonConverterFactory.create()
    private var apiWithToken: ApiService? = null
    private var apiWithoutToken: ApiService? = null

    val apiServiceWithToken get() = apiWithToken
    val apiServiceWithoutToken get() = apiWithoutToken

    companion object {
        fun create(token: String) : ServerCommunication {
            return ServerCommunication(token)
        }
    }

    init {
        updateToken(token!!)
    }

    /**
     * Вызывать при обновлении токена.
     * Пересобирает все сетевые объекты для общения с сетью
    * */
    fun updateToken(_token: String){
        token = _token
        httpClientBuilder = buildHttpClientBuilder()
        apiWithToken = buildClientWithToken()
        apiWithoutToken = buildClientWithoutToken()
    }

    private fun buildHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            addInterceptor(OAuthInterceptor("Bearer", token ?: ""))
            Log.d(javaClass.simpleName, "buildHttpClientBuilder with token: ${token}")
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
    }

    private fun buildClientWithToken(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory!!)
            .addCallAdapterFactory(coroutineAdapterFactory!!)
            .client(httpClientBuilder?.build()!!)
            .build()
            .create(ApiService::class.java)
    }

    private fun buildClientWithoutToken(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory!!)
            .addCallAdapterFactory(coroutineAdapterFactory!!)
            .client(httpClientBuilder?.build()!!)
            .build()
            .create(ApiService::class.java)
    }

    fun destroy(){
        coroutineAdapterFactory = null
        httpClientBuilder = null
        gsonConverterFactory = null
        apiWithToken = null
        apiWithoutToken = null
        token = null
    }
}