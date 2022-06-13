package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.android.billingclient.api.SkuDetails
import com.uzlov.dating.lavada.domain.models.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface ISubscriptionsDataSource {
    suspend fun getAllSubscriptions() : List<SkuDetails>
    suspend fun getAvailableSubscriptions() : List<SkuDetails>
    suspend fun getCurrentSubscription(uidUser: String)
    suspend fun putSubscription(subscription: Subscription)
    suspend fun destroy()
}