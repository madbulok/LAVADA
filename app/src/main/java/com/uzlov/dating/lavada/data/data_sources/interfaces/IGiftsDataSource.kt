package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.CategoryGifts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface IGiftsDataSource {

    @ExperimentalCoroutinesApi
    suspend fun getCategoryByID(id: String): Flow<CategoryGifts?>

    suspend fun sendGift(token: String, map: Map<String, String>)
    suspend fun getALlGifts(token: String)
    suspend fun postPurchase(token: String, map: Map<String, String>)
    suspend fun getListGifts(token: String, limit: String, offset: String, status: String)
    suspend fun getListReceivedGifts(token: String, limit: String, offset: String)
}