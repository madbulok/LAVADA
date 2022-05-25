package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Chat(
    var uuid: String = UUID.randomUUID().toString(),
    var members: MutableList<String> = mutableListOf(),
    var messages: MutableList<ChatMessage> = mutableListOf()
) : Parcelable

@Parcelize
data class ReChat(
    @Expose val status: String? = null,
    @Expose val data: RemoteChat? = null,
) :Parcelable

@Parcelize
data class  RemoteChat(
    val chat_id: String? = null,
    val chat_user_id: String? = null,
    val chat_to_user_id: String? = null,
    val chat_status: String? = null,
    val created_at: String? = null,
    val _count_messages: String? = null,
    val has_chat: String? = null
) : Parcelable

@Parcelize
data class RemoteChatList(
    @Expose val status: String? = null,
    @Expose val data: DataChat? = DataChat(),
) : Parcelable

@Parcelize
data class DataChat(
    var rows: List<RemoteChat?>? = null,
    var count_rows: Int? = null
) : Parcelable
