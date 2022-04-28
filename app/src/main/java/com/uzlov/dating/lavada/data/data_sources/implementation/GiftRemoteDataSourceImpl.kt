package com.uzlov.dating.lavada.data.data_sources.implementation

import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import com.uzlov.dating.lavada.retrofit.IServerDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GiftRemoteDataSourceImpl @Inject constructor(val userRemoteDataSourceImpl: IServerDataSource<Any>) :
    IGiftsDataSource {

    @ExperimentalCoroutinesApi
    override suspend fun getCategoryByID(id: String): Flow<CategoryGifts?> {
        return flow {
            emit(CategoryGifts())
        }
    }

    override suspend fun sendGift(token: String, map: Map<String, String>) {
        userRemoteDataSourceImpl.sendGift(token, map)
    }

    override suspend fun getALlGifts(token: String) {
        userRemoteDataSourceImpl.getALlGifts(token)
    }

    override suspend fun postPurchase(token: String, map: Map<String, String>) {
        userRemoteDataSourceImpl.postPurchase(token, map)
    }

    override suspend fun getListGifts(
        token: String,
        limit: String,
        offset: String,
        status: String
    ) {
        userRemoteDataSourceImpl.getListGifts(token, limit, offset, status)
    }

    override suspend fun getListReceivedGifts(token: String, limit: String, offset: String) {
        userRemoteDataSourceImpl.getListReceivedGifts(token, limit, offset)
    }
}