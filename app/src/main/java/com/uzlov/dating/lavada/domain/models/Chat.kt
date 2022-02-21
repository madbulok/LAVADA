package com.uzlov.dating.lavada.domain.models

import java.util.*
import kotlin.collections.ArrayList

data class Chat(
    val uuid: String = UUID.randomUUID().toString(),
    val members: ArrayList<String>,
    val messages: ArrayList<ChatMessage>
)