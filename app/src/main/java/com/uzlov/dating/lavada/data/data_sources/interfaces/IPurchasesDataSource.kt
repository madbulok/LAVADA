package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.android.billingclient.api.SkuDetails
import com.uzlov.dating.lavada.domain.models.Purchase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface IPurchasesDataSource {
    suspend fun getPurchases(uidUser: String) : Flow<List<Purchase>>
    suspend fun putPurchase(purchase: Purchase)
    suspend fun getAllPurchases() : List<SkuDetails>
}