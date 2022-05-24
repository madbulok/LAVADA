package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    var id: Long = System.currentTimeMillis() / 1000,
    var datetime: Long = System.currentTimeMillis() / 1000,
    var message: String = "",
    var sender: String = "",
    var viewed: Boolean = false,
    var mediaUrl: String? = null
) : Parcelable

@Parcelize
data class ReMessage(
    @Expose val status: String? = null,
    @Expose val data: DataMessage? = null,
) :Parcelable

@Parcelize
data class  RemoteMessage(
    val msg_id: String? = null,
    val user_id: String? = null,
    val gift_id: String? = null,
    val msg_status: String? = null,
    val msg_text: String? = null,
    val msg_file: String? = null,
    val created_at: String? = null
) : Parcelable

@Parcelize
data class DataMessage(
    var rows: List<RemoteMessage?>? = null,
    var count_rows: Int? = null
) : Parcelable