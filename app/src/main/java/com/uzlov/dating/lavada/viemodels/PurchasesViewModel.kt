package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.domain.models.Purchase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PurchasesViewModel @Inject constructor(var purchaseRepository: IPurchasesDataSource) : ViewModel()  {

    private val result = MutableLiveData<List<Purchase>>()
    private val _allPurchases = MutableLiveData<List<SkuDetails>>()
    val allPurchases get(): LiveData<List<SkuDetails>> = _allPurchases


    fun getAllPurchases() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = purchaseRepository.getAllPurchases()
            _allPurchases.postValue(result)
        }
    }

    fun getPurchases(uidUser: String) : LiveData<List<Purchase>> {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.getPurchases(uidUser).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun putPurchase(purchase: Purchase) {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.putPurchase(purchase)
        }
    }
}