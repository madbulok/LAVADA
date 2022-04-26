package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface ISubscriptionsDataSource {
    suspend fun getAvailableSubscriptions() : List<Subscription>
    suspend fun getCurrentSubscription(uidUser: String)
    suspend fun putSubscription(subscription: Subscription)
}