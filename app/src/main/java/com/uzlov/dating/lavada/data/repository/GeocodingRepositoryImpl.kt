package com.uzlov.dating.lavada.data.repository

import com.uzlov.dating.lavada.domain.models.GeoPoint
import retrofit2.Response
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(private var api: GeoApi) : IGeocodingRepository {

    override suspend fun getAddress(latitude: String, longitude: String): Response<GeoPoint?> {
        return api.getGeocodingAddress(latitude = latitude, longitude = longitude)
    }
}