package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
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