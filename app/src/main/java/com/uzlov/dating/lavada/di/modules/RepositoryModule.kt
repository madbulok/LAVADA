package com.uzlov.dating.lavada.di.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.uzlov.dating.lavada.app.App
import com.uzlov.dating.lavada.data.OAuthInterceptor
import com.uzlov.dating.lavada.retrofit.ApiService
import com.uzlov.dating.lavada.retrofit.BaseInterceptor
import com.uzlov.dating.lavada.retrofit.RemoteDataSource
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

const val BASE_URL = "http://vm3355680.52ssd.had.wf/"

@Module
class RepositoryModule {

    companion object {
        const val RETROFIT_WITH_TOKEN = "RETROFIT_WITH_TOKEN"
        const val RETROFIT_WITHOUT_TOKEN = "RETROFIT_WITHOUT_TOKEN"
        const val CLIENT_WITH_TOKEN = "CLIENT_WITH_TOKEN"
        const val CLIENT_WITHOUT_TOKEN = "CLIENT_WITHOUT_TOKEN"
        const val TOKEN = "TOKEN"
    }

    @Provides
    @Singleton
    @Named(TOKEN)
    fun provideToken() = App.getToken()

    @Provides
    @Singleton
    fun provideCoroutineCallAdapterFactory() = CoroutineCallAdapterFactory()

    @Provides
    @Singleton
    @Named(RETROFIT_WITH_TOKEN)
    fun createRetrofitWithToken(
        @Named(CLIENT_WITH_TOKEN) okHttpClient: OkHttpClient,
        gson: GsonConverterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gson)
            .addCallAdapterFactory(coroutineCallAdapterFactory)
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @Named(RETROFIT_WITHOUT_TOKEN)
    fun createRetrofit(
        gson: GsonConverterFactory,
        @Named(CLIENT_WITHOUT_TOKEN) okHttpClient: OkHttpClient,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gson)
            .addCallAdapterFactory(coroutineCallAdapterFactory)
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(interceptor: Interceptor) : OkHttpClient.Builder = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    @Provides
    @Singleton
    @Named(CLIENT_WITH_TOKEN)
    fun createOkHttpClientWithToken(@Named(TOKEN) token: String, httpClient: OkHttpClient.Builder): OkHttpClient {
        httpClient.addInterceptor(OAuthInterceptor("Bearer", token))
        return httpClient.build()
    }

    @Provides
    @Singleton
    @Named(CLIENT_WITHOUT_TOKEN)
    fun createOkHttpClient(httpClient: OkHttpClient.Builder): OkHttpClient {
        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideInterceptor() : Interceptor = BaseInterceptor()


    // data sources and etc.
    @Provides
    @Singleton
    fun provideDataSourceRemote(
        @Named(RETROFIT_WITH_TOKEN)  apiWithToken: ApiService,
        @Named(RETROFIT_WITHOUT_TOKEN)  apiWithOutToken: ApiService,
    ) = RemoteDataSource(apiWithToken, apiWithOutToken)

}