package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Chat(
    var uuid: String = UUID.randomUUID().toString(),
    var members: MutableList<String> = mutableListOf(),
    var messages: MutableList<ChatMessage> = mutableListOf()
) : Parcelable


