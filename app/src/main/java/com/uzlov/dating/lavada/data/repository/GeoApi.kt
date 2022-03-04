package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.domain.models.GeoPoint
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {
    @GET("reverse")
    suspend fun getGeocodingAddress(
        @Query("format") format: String = "json",
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?,
        @Query("addressdetails") addressdetails: String = "1"
    ): Response<GeoPoint?>
}