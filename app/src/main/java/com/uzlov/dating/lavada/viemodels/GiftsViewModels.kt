package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GiftsViewModels @Inject constructor(private val giftsRepository: IGiftsDataSource) : ViewModel() {

    private val result = MutableLiveData<List<CategoryGifts>>()
    private val resultById = MutableLiveData<CategoryGifts?>()

    fun getCategoryByID(id: String): MutableLiveData<CategoryGifts?> {
        viewModelScope.launch {
            giftsRepository.getCategoryByID(id)
                .collect {
                    resultById.postValue(it)
                }
        }
        return resultById
    }

    fun sendGift(token: String, map: Map<String, String>){
        viewModelScope.launch(Dispatchers.IO) {
            giftsRepository.sendGift(token, map)
        }
    }

    fun getALlGifts(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            giftsRepository.getALlGifts(token)
        }
    }

    fun postPurchase(token: String, map: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            giftsRepository.postPurchase(token, map)
        }
    }

    fun getListGifts(token: String, limit: String, offset: String, status: String){
        viewModelScope.launch(Dispatchers.IO) {
            giftsRepository.getListGifts(token, limit, offset, status)
        }
    }

    fun getListReceivedGifts(token: String, limit: String, offset: String){
        viewModelScope.launch(Dispatchers.IO) {
            giftsRepository.getListReceivedGifts(token, limit, offset)
        }
    }
}