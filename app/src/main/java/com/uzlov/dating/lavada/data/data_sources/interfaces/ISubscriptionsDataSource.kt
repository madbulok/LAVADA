package com.uzlov.dating.lavada.data.data_sources.interfaces

import com.uzlov.dating.lavada.domain.models.Subscription
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface ISubscriptionsDataSource {
    @ExperimentalCoroutinesApi
    suspend fun getSubscriptions(uidUser: String) : Flow<List<Subscription>>
    suspend fun putSubscription(subscription: Subscription)
}