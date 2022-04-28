package com.uzlov.dating.lavada.data.data_sources.implementation

import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.domain.models.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PurchasesRemoteDataSourceImpl @Inject constructor() :
    IPurchasesDataSource {

    override suspend fun getPurchases(uidUser: String): Flow<List<Purchase>> = flow {

    }

    override suspend fun putPurchase(purchase: Purchase) {

    }
}