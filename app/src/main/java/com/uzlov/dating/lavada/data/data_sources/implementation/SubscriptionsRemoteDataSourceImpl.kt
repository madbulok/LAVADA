package com.uzlov.dating.lavada.data.data_sources.implementation

import android.util.Log
import com.android.billingclient.api.*
import com.uzlov.dating.lavada.data.data_sources.interfaces.ISubscriptionsDataSource
import com.uzlov.dating.lavada.domain.models.Subscription
import com.uzlov.dating.lavada.retrofit.ApiService
import kotlinx.coroutines.flow.flow
import java.lang.Exception

import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class SubscriptionsRemoteDataSourceImpl @Inject constructor(private val billingClientBuilder: BillingClient.Builder) :
    ISubscriptionsDataSource {

    private var billingClient: BillingClient? = null
    // callback about purchase!
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
        billingClient = billingClientBuilder.setListener(purchasesUpdateListener).build()
    }

    override suspend fun getAllSubscriptions() = suspendCoroutine<List<SkuDetails>> {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                when(result.responseCode){
                    BillingClient.BillingResponseCode.OK -> {
                        val param = SkuDetailsParams.newBuilder().apply {
                            setSkusList(listOf("subs_lavada_1"))
                            setType(BillingClient.SkuType.SUBS)
                        }

                        billingClient?.querySkuDetailsAsync(param.build()){ p0, listSku ->
                            if (!listSku.isNullOrEmpty()){
                                it.resumeWith(Result.success(listSku))
                                listSku.firstOrNull()
                            } else {
                                it.resumeWithException(Exception("No subs!"))
                            }
                        }
                    }
                    BillingClient.BillingResponseCode.ERROR -> {
                        it.resumeWithException(Exception("Error"))
                    }
                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                        it.resumeWithException(Exception("Developer error"))
                    }
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                        it.resumeWithException(Exception("BILLING_UNAVAILABLE"))
                    }
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        it.resumeWithException(Exception("ITEM_ALREADY_OWNED"))
                    }
                    else -> {
                        it.resumeWithException(Exception("User cancelled"))
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
            }
        })
    }

    override suspend fun getAvailableSubscriptions() = suspendCoroutine<List<SkuDetails>> {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                when(result.responseCode){
                    BillingClient.BillingResponseCode.OK -> {
                        val param = SkuDetailsParams.newBuilder().apply {
                            setSkusList(listOf("subs_lavada_1"))
                            setType(BillingClient.SkuType.SUBS)
                        }

                        billingClient?.querySkuDetailsAsync(param.build()){ p0, listSku ->
                            if (!listSku.isNullOrEmpty()){
                                Log.e(javaClass.simpleName, "onBillingSetupFinished: ${listSku[0]}")
                                it.resumeWith(Result.success(listSku))
                            } else {
                                it.resumeWithException(Exception("No subs!"))
                            }
                            listSku?.forEach {
                                Log.e("getAvailable()", "onBillingSetupFinished: ${it}")
                            }
                        }
                    }
                    else -> {

                    }
                }
            }

            override fun onBillingServiceDisconnected() {
            }
        })
    }


    override suspend fun getCurrentSubscription(uidUser: String) {

    }

    override suspend fun putSubscription(subscription: Subscription) {

    }

    override suspend fun destroy() {
        billingClient?.endConnection()
    }
}