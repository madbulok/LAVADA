package com.uzlov.dating.lavada.di.modules


import com.uzlov.dating.lavada.data.repository.GeoApi
import com.uzlov.dating.lavada.data.repository.GeocodingRepositoryImpl
import com.uzlov.dating.lavada.data.repository.IGeocodingRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GeoModule {

    @Provides
    fun baseUrl() = "https://nominatim.openstreetmap.org/"

    @Provides
    fun gson(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideService() : Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl())
        .addConverterFactory(gson())
        .client(client())
        .build()

    @Provides
    fun provideGeocodingApi(retrofit: Retrofit): GeoApi = retrofit.create(GeoApi::class.java)

    @Provides
    fun provideGeoRepository(api: GeoApi) : IGeocodingRepository = GeocodingRepositoryImpl(api)

    @Singleton
    @Provides
    fun client(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
}