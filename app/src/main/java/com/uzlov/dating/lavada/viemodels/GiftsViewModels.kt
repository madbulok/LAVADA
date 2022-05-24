import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.di.modules.ServerCommunication
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import com.uzlov.dating.lavada.domain.models.ReGift
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GiftsViewModels @Inject constructor(
    private val giftsRepository: IGiftsDataSource,
    private val serverCommunication: ServerCommunication
) : ViewModel() {
    private val resultById = MutableLiveData<CategoryGifts?>()
    private val reGift = MutableLiveData<ReGift>()

    @ExperimentalCoroutinesApi
    fun getCategoryByID(id: String): MutableLiveData<CategoryGifts?> {
        viewModelScope.launch {
            giftsRepository.getCategoryByID(id)
                .collect {
                    resultById.postValue(it)
                }
        }
        return resultById
    }


    /**
     * получить список подарков
     * @param token - token, полученный от бэка
     * */
    fun getAllGifts(token: String): LiveData<ReGift?> {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reGift.postValue(serverCommunication.apiServiceWithToken?.getAllGiftsAsync()?.await())


        }
        return reGift
    }

    /**
     * подарить подарок
     * @param token токен от бэка
     * @param firebaseUid fbUID пользователя, которому будем дарить подарок
     * @param ugId ID подарка, который будем дарить
     * */
    fun sendGift(token: String, firebaseUid: String, ugId: String): LiveData<ReGift?> {
        viewModelScope.launch(Dispatchers.IO) {
            val body = mutableMapOf<String, String>()
            body["firebase_uid"] = firebaseUid
            body["ug_id"] = ugId
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reGift.postValue(serverCommunication.apiServiceWithToken?.sendGiftAsync(body)?.await())
        }
        return reGift
    }

    /**
     * купить подарок
     * @param token токен от бэка
     * @param giftId ID подарка, который будем покупать
     * */
    fun buyGift(token: String, giftId: String): LiveData<ReGift> {
        viewModelScope.launch(Dispatchers.IO) {
            val body = mutableMapOf<String, String>()
            body["gift_id"] = giftId
            body["meta"] = "{}"
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reGift.postValue(
                serverCommunication.apiServiceWithToken?.postPurchaseAsync(body)?.await()
            )
        }
        return reGift
    }

    /**
     * просмотр списка купленных подарков
     * @param token токен от бэка
     * @param status может быть active (уже куплен, но еще не подарен) или activated (уже подарен)
     * */
    fun getPurchasedGifts(token: String, status: String): LiveData<ReGift> {
        viewModelScope.launch(Dispatchers.IO) {
            val limit = "20"
            val offset = "0"
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reGift.postValue(
                serverCommunication.apiServiceWithToken?.getListGiftsAsync(
                    limit,
                    offset,
                    status
                )?.await()
            )
        }
        return reGift
    }

    /**
     * просмотр списка подарков, которые подарены пользователю
     * @param token токен от бэка
     * */
    fun getDonatedGifts(token: String): LiveData<ReGift>{
        viewModelScope.launch(Dispatchers.IO) {
            val limit = "20"
            val offset = "0"
            serverCommunication.updateToken(token) // обновляем токен полученый с сервера
            reGift.postValue(
                serverCommunication.apiServiceWithToken?.getListReceivedGiftsAsync(limit, offset)?.await()
            )
        }
        return reGift
    }
}