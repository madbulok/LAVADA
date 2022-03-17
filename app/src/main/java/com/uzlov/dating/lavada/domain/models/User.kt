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
    var ready: Boolean? = null,
    var premium: Boolean? = null,
    var balance: Int = 0
) : Parcelable