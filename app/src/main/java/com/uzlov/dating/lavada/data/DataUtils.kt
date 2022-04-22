package com.uzlov.dating.lavada.data

import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.ReUser
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User


fun convertDtoToModel(remoteUser: RemoteUser): User {
    val fact: ReUser = remoteUser.data
    val male  = when (fact.user_gender) {
        "MALE" -> MALE.MAN
        "FEMALE" -> MALE.WOMAN
        "ANOTHER" -> MALE.ANOTHER
        else -> MALE.ANOTHER
    }
    //что-то с премиумом нужно решить
    val premium = false
    return User(
        uid = fact.user_firebase_uid!!,
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
        premium = premium,
    )
}
