package com.uzlov.dating.lavada.domain.models

data class ChatMessage(
    var id: Long,
    var datetime: Long,
    var message: String,
    var sender: String,
    var viewed: Boolean = false,
    var mediaUrl: String?
)