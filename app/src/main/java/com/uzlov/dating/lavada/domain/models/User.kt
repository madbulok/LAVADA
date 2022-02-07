package com.uzlov.dating.lavada.domain.models

enum class MALE {
    MAN,
    WOMAN,
    ANOTHER
}

data class User(
    var name: String,
    val male: MALE,
    var age: Int,
    var about: String,
    var url_avatar: String,
    var url_video: String
)