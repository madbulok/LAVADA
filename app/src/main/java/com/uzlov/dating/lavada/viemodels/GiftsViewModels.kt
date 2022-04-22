package com.uzlov.dating.lavada.viemodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GiftsViewModels @Inject constructor(var giftsRepository: IGiftsDataSource) : ViewModel() {

    private val result = MutableLiveData<List<CategoryGifts>>()
    private val resultById = MutableLiveData<CategoryGifts?>()

    fun getCategoryGifts(): MutableLiveData<List<CategoryGifts>> {
        viewModelScope.launch {
            giftsRepository.getCategoryGifts()
                .collect {
                    result.postValue(it)
                }
        }

        return result
    }

    fun getCategoryByID(id: String): MutableLiveData<CategoryGifts?> {
        viewModelScope.launch {
            giftsRepository.getCategoryByID(id)
                .collect {
                    resultById.postValue(it)
                }
        }
        return resultById
    }

    suspend fun sendGift(token: String, map: Map<String, String>) = giftsRepository.sendGift(token, map)
    suspend fun getALlGifts(token: String) = giftsRepository.getALlGifts(token)
    suspend fun postPurchase(token: String, map: Map<String, String>) = giftsRepository.postPurchase(token, map)
    suspend fun getListGifts(token: String, limit: String, offset: String, status: String) = giftsRepository.getListGifts(token, limit, offset, status)
    suspend fun getListReceivedGifts(token: String, limit: String, offset: String) = giftsRepository.getListReceivedGifts(token, limit, offset)

}