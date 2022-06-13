package com.uzlov.dating.lavada.data.data_sources.implementation

import android.util.Log
import com.android.billingclient.api.*
import com.uzlov.dating.lavada.app.Extensions
import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.domain.models.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PurchasesRemoteDataSourceImpl @Inject constructor(billingClientBuilder: BillingClient.Builder) :
    IPurchasesDataSource {

    private var billingClient: BillingClient? = null
    private val purchasesUpdateListener = PurchasesUpdatedListener { result, purchase ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // успешная покупка, сохраняем её в бд
                purchase?.forEach {
                    Log.e(javaClass.simpleName, it.packageName)
                }
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                Log.e(javaClass.simpleName, "ITEM_UNAVAILABLE")
            }

            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Log.e(javaClass.simpleName, "BILLING_UNAVAILABLE")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.e(javaClass.simpleName, "ITEM_ALREADY_OWNED")
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.e(javaClass.simpleName, "USER_CANCELED")
            }
            BillingClient.BillingResponseCode.ERROR -> {
                Log.e(javaClass.simpleName, "ERROR")
            }
        }
    }

    init {
        billingClient = billingClientBuilder.setListener(purchasesUpdateListener)
                .enablePendingPurchases()
                .build()
    }

    override suspend fun getPurchases(uidUser: String): Flow<List<Purchase>> = flow {

    }

    override suspend fun putPurchase(purchase: Purchase) {

    }

    override suspend fun getAllPurchases(): List<SkuDetails> = suspendCoroutine {
        val param = SkuDetailsParams.newBuilder().apply {
            setSkusList(Extensions.getPurchasesId())
            setType(BillingClient.SkuType.INAPP)
        }
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                when (result.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        billingClient?.querySkuDetailsAsync(param.build()){ _, listSku ->
                            if (!listSku.isNullOrEmpty()){
                                Log.e(javaClass.simpleName, "onBillingSetupFinished: ${listSku[0]}")
                                it.resumeWith(Result.success(listSku))
                            } else {
                                it.resumeWithException(Exception("No subs!"))
                            }
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {

            }
        })
    }
}