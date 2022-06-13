package com.uzlov.dating.lavada.di.modules

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.uzlov.dating.lavada.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BillingModule {

    @Singleton
    @Provides
    fun billingClient(context: Context) : BillingClient.Builder =
        BillingClient.newBuilder(context)
            .enablePendingPurchases()
}