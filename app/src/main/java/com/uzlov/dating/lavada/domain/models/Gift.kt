package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import java.util.*

class Gift(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var descriptions: String = "",
    var cost: Int = 0
)

@Parcelize
data class ReGift(
    @Expose val status: String? = null,
    @Expose val data: DataGift? = null,
) : Parcelable

@Parcelize
data class  RemoteGift(
    val gift_id: String? = null,
    val gift_key: String? = null,
    val gift_title: String? = null,
    val gift_desc: String? = null,
    val gift_price: String? = null,
    val gift_meta: String? = null,
    val created_at: String? = null
) : Parcelable


@Parcelize
data class DataGift(
    var rows: List<RemoteGift?>? = null,
    var count_rows: Int? = null
) : Parcelable


//ответ на покупку подарка
//{"status":"ok","data":{"ug_id":1,"user_id":null,"ug_pay_user_id":4,"gift_id":1,"ug_status":"active","ug_meta":"{}","activated_at":null,"created_at":"2022-05-24T12:47:05.000Z"}}
