package com.uzlov.dating.lavada.viemodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.repository.IGeocodingRepository
import com.uzlov.dating.lavada.domain.models.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeocodingViewModel @Inject constructor(private var geoRepository: IGeocodingRepository) :
    ViewModel() {

    private val result = MutableLiveData<GeoPoint>()

    fun fetchGeocoding(lat: String, lng: String): LiveData<GeoPoint> {
        //отменяем предыдущие запросы
        viewModelScope.coroutineContext.cancelChildren()

        // запрашиваем новый адрес
        viewModelScope.launch {
            val response = geoRepository.getAddress(latitude = lat, longitude = lng)
            withContext(Dispatchers.IO) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        result.postValue(it)
                    }
                } else {
                    Log.e(javaClass.simpleName, "fetchGeocoding: ERROR")
                }
            }
        }
        return result
    }
}