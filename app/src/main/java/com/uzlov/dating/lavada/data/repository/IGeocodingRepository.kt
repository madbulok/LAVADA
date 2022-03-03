package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.domain.models.GeoPoint
import retrofit2.Response

interface IGeocodingRepository {
    suspend fun getAddress(latitude: String, longitude: String): Response<GeoPoint?>
}