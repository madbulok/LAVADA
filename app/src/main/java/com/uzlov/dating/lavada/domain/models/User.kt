package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

enum class MALE {
    MAN,
    WOMAN,
    ANOTHER
}

@Parcelize
data class User(
    var uid: String = UUID.randomUUID().toString(),
    var email: String? = "",
    var password: String? = "",
    var name: String? = "",
    var male: MALE? = null,
    var age: Int? = 0,
    var about: String? = "",
    var url_avatar: String? = "",
    var url_video: String? = "",
    var lat: Double? = 0.0,
    var lon: Double? = 0.0,
    var matches: MutableMap<String, Boolean> = mutableMapOf(), // хранит лайки пользователей <UID друга , взаимно или нет>
    var chats: MutableMap<String, String> = mutableMapOf(),// хранит чаты где он есть <UID собеседника , свой UID>
    var dist: Double? = 0.0, //хранит расстояние между ним и тем, кому его показывать
    var location: String? = "",
    var black_list: MutableList<String> = mutableListOf(), //хранит UID для блэклиста
    var ready: Boolean = false,
    var premium: Boolean = false,
    var balance: Int = 0,
    var userId: String? = ""
) : Parcelable

fun User.getNAmeLabel() = if (name.isNullOrBlank() && age == null) {
    "Безымянный пользователь"
} else if (name.isNullOrBlank() && age != null) {
    "Без имени, $age"
}else if (!name.isNullOrBlank() && age == null) {
    name
} else {
    "$name, $age"
}

@Parcelize
data class RemoteUser(
    val status: String? = null,
    val data: ReUser? = null,
) : Parcelable

@Parcelize
data class RemoteUserList(
    val status: String? = null,
    val data: Data? = Data(),
) : Parcelable

@Parcelize
data class ReUser(
    val user_id: String? = null,
    val user_firebase_uid: String? = null,
    val user_status: String? = null,
    val user_email: String? = null,
    val user_nickname: String? = null,
    val user_firstname: String? = null,
    val user_lastname: String? = null,
    val user_photo: String? = null,
    val user_video: String? = null,
    val user_location_lat: String? = null,
    val user_location_lng: String? = null,
    val user_fb: String? = null,
    val user_vk: String? = null,
    val user_ok: String? = null,
    val user_instagram: String? = null,
    val user_twitter: String? = null,
    val user_age: String? = null,
    val user_gender: String? = null,
    val user_address: String? = null,
    val _has_premium: Boolean = false,
    val token: String? = null,
    val user_balance: String? = null,
    val user_description: String? = null,
    val _mutual_like: String? = null
) : Parcelable

@Parcelize
data class Data(
    var rows: List<ReUser?>? = null,
    var count_rows: Int? = null
) : Parcelable