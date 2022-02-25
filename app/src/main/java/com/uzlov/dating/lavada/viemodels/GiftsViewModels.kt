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
}