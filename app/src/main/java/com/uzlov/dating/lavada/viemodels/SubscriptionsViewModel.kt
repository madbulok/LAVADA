package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.ISubscriptionsDataSource
import com.uzlov.dating.lavada.domain.models.Purchase
import com.uzlov.dating.lavada.domain.models.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(var subsRepository: ISubscriptionsDataSource) : ViewModel()  {

    private val result = MutableLiveData<List<Subscription>>()

    fun getSubscriptions(uidUser: String) : LiveData<List<Subscription>>{
        viewModelScope.launch(Dispatchers.IO) {
            subsRepository.getSubscriptions(uidUser).collect {
                result.postValue(it)
            }
        }
        return result
    }
    fun putSubscription(subscription: Subscription) {
        viewModelScope.launch(Dispatchers.IO) {
            subsRepository.putSubscription(subscription)
        }
    }
}