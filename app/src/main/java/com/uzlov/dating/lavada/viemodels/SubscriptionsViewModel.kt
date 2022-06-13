package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.uzlov.dating.lavada.data.data_sources.interfaces.ISubscriptionsDataSource
import com.uzlov.dating.lavada.domain.PremiumState
import com.uzlov.dating.lavada.domain.models.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(var subsRepository: ISubscriptionsDataSource) : ViewModel()  {

    @Deprecated("")
    private val result = MutableLiveData<List<Subscription>>()

    private val responseSubs = MutableLiveData<PremiumState<Subscription>>()
    private val responsePurchase = MutableLiveData<PremiumState<Purchase>>()

    // все подписки которые зарегистрированы на play markets
    private val allSubscriptions = MutableLiveData<List<SkuDetails>>()
    val allSubs get(): LiveData<List<SkuDetails>> = allSubscriptions

    fun getAllSubscriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = subsRepository.getAvailableSubscriptions()
            allSubscriptions.postValue(result)
        }
    }

    fun getAvailableSubscriptions(uidUser: String) : LiveData<List<Subscription>>{
        viewModelScope.launch(Dispatchers.IO) {
            subsRepository.getAvailableSubscriptions()
        }
        return result
    }

    fun getCurrentSubscription(uidUser: String) : LiveData<PremiumState<Subscription>> {
        viewModelScope.launch(Dispatchers.IO) {
            subsRepository.getCurrentSubscription(uidUser)
        }
        return responseSubs
    }
    fun putSubscription(subscription: Subscription) {
        viewModelScope.launch(Dispatchers.IO) {
            subsRepository.putSubscription(subscription)
        }
    }
}