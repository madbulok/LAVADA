package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MALE {
    MAN,
    WOMAN,
    ANOTHER
}
@Parcelize
data class User(
    val id: String,
    var name: String,
    val male: MALE,
    var age: Int,
    var about: String,
    var url_avatar: String,
    var url_video: String,
    var lat: Double,
    var lon: Double
): Parcelable