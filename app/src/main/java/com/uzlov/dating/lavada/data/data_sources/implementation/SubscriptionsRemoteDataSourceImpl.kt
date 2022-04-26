package com.uzlov.dating.lavada.data.data_sources.implementation

import com.uzlov.dating.lavada.data.data_sources.interfaces.ISubscriptionsDataSource
import com.uzlov.dating.lavada.domain.models.Subscription
import com.uzlov.dating.lavada.retrofit.ApiService

import javax.inject.Inject


class SubscriptionsRemoteDataSourceImpl @Inject constructor(private val apiServerValue: ApiService) :
    ISubscriptionsDataSource {

    override suspend fun getAvailableSubscriptions(): List<Subscription> {
        return emptyList()
    }

    override suspend fun getCurrentSubscription(uidUser: String) {

    }

    override suspend fun putSubscription(subscription: Subscription) {

    }
}