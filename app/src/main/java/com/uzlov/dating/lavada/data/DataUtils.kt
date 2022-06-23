package com.uzlov.dating.lavada.data

import android.os.Parcelable
import com.uzlov.dating.lavada.domain.models.MALE
import com.uzlov.dating.lavada.domain.models.ReUser
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import kotlinx.parcelize.Parcelize
import java.util.*

const val BASE_STORAGE_URL = "https://708327.selcdn.ru/"

fun convertDtoToModel(remoteUser: RemoteUser): User {
    val fact: ReUser? = remoteUser.data
    val male  = when (fact?.user_gender) {
        "male" -> MALE.MAN
        "female" -> MALE.WOMAN
        "another" -> MALE.ANOTHER
        else -> MALE.MAN
    }
    val ready = when(fact?.user_status){
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
        ready = ready
    )
}

fun convertListDtoToModel(reUser: ReUser?): User {
    val fact: ReUser? = reUser
    val male  = when (fact?.user_gender) {
        "MALE" -> MALE.MAN
        "FEMALE" -> MALE.WOMAN
        "ANOTHER" -> MALE.ANOTHER
        else -> MALE.ANOTHER
    }
    val ready = when(fact?.user_status){
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
        ready = ready
    )
}

fun convertToLike(reUser: ReUser?): Boolean{
    val fact: ReUser? = reUser
    val like = when(fact?._mutual_like){
        "1" -> true
        "2" -> false
        else -> false
    }
    return like
}

fun convertToReady(reUser: ReUser?): Boolean{
    val fact: ReUser? = reUser
    val ready = when(fact?.user_status){
        "active" -> true
        "not_confirmed" -> false
        else -> false
    }
    return ready
}

