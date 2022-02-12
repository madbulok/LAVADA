package com.uzlov.dating.lavada.di.modules

import com.android.billingclient.api.BillingClient
import com.uzlov.dating.lavada.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BillingModule {

    @Singleton
    @Provides
    private fun billingClient(context: App) : BillingClient =
        BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .build()
}