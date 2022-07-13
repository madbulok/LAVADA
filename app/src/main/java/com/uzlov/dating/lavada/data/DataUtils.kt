package com.uzlov.dating.lavada.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.uzlov.dating.lavada.domain.models.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val BASE_STORAGE_URL = "https://708327.selcdn.ru/"

fun convertDtoToModel(remoteUser: RemoteUser): User {
    val fact: ReUser? = remoteUser.data
    val male = when (fact?.user_gender) {
        "male" -> MALE.MAN
        "female" -> MALE.WOMAN
        "another" -> MALE.ANOTHER
        else -> MALE.MAN
    }
    val ready = when (fact?.user_status) {
        "active" -> true
        "not_confirmed" -> false
        else -> false
    }
    return User(
        uid = fact?.user_firebase_uid!!,
        email = fact.user_email,
        name = fact.user_firstname,
        male = male,
        age = fact.user_age?.toInt(),
        about = fact.user_description,
        url_avatar = fact.user_photo,
        url_video = fact.user_video,
        lat = fact.user_location_lat?.toDouble(),
        lon = fact.user_location_lng?.toDouble(),
        location = fact.user_address,
        premium = fact._has_premium,
        ready = ready,
        userId = fact.user_id
    )
}

fun convertListDtoToModel(reUser: ReUser?): User {
    val fact: ReUser? = reUser
    val male = when (fact?.user_gender) {
        "MALE" -> MALE.MAN
        "FEMALE" -> MALE.WOMAN
        "ANOTHER" -> MALE.ANOTHER
        else -> MALE.ANOTHER
    }
    val ready = when (fact?.user_status) {
        "active" -> true
        "not_confirmed" -> false
        else -> false
    }

    return User(
        uid = fact?.user_firebase_uid!!,
        email = fact.user_email,
        name = fact.user_nickname,
        male = male,
        age = fact.user_age?.toInt(),
        about = fact.user_description,
        url_avatar = fact.user_photo,
        url_video = fact.user_video,
        lat = fact.user_location_lat?.toDouble(),
        lon = fact.user_location_lng?.toDouble(),
        location = fact.user_address,
        premium = fact._has_premium,
        ready = ready,
        userId = fact.user_id
    )
}

fun convertToLike(reUser: ReUser?): Boolean {
    val fact: ReUser? = reUser
    val like = when (fact?._mutual_like) {
        "1" -> true
        "2" -> false
        else -> false
    }
    return like
}

fun convertToReady(reUser: ReUser?): Boolean {
    val fact: ReUser? = reUser
    val ready = when (fact?.user_status) {
        "active" -> true
        "not_confirmed" -> false
        else -> false
    }
    return ready
}

fun displayApiResponseErrorBody(response: Response<*>): String {
    var finish: String? = null
    val gson = GsonBuilder().create()
    val mError = gson.fromJson(
        response.errorBody()!!.string(),
        ErrorUtils::class.java
    )
    if (mError.error?.user_status != null) {
        finish = mError.error.user_status
    }
    if (mError.error?.message != null) {
        finish = mError.error.message
    }

    return finish ?: "Неизвестная ошибка"
}

fun convertListReMessageToChatMessage(remoteMessage: RemoteMessage): ChatMessage {
    val fact: RemoteMessage = remoteMessage
    val viewed: Boolean = when (fact.msg_status) {
        "1" -> true
        "0" -> false
        else -> false
    }
    val media: String? = when (fact.msg_file) {
        null -> null
        "" -> null
        else -> fact.msg_file
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ChatMessage(
            id = fact.msg_id!!.toLong(),
            datetime = Instant.parse(fact.created_at).toEpochMilli(),
            message = fact.msg_text!!,
            sender = fact.user_id!!,
            viewed = viewed,
            mediaUrl = media
        )
    } else {
        ChatMessage(
            id = fact.msg_id!!.toLong(),
            datetime = convertDateToLong(fact.created_at!!)!!,
            message = fact.msg_text!!,
            sender = fact.user_id!!,
            viewed = viewed,
            mediaUrl = media
        )
    }
}

fun convertDateToLong(date: String): Long? {
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    return df.parse(date)?.time
}

fun convertReChatToChat(remoteChat: RemoteChat): Chat {
    val members = mutableListOf<String>()
    remoteChat.chat_user_id?.let { members.add(it) }
    remoteChat.chat_to_user_id?.let { members.add(it) }
    return Chat(
        uuid = remoteChat.chat_id!!,
        members = members,
        messages = mutableListOf()
    )
}

fun convertChatToMappedChat(chat: Chat, user: User) : MappedChat{
    return MappedChat(
        companion = user,
        chat = chat
    )
}