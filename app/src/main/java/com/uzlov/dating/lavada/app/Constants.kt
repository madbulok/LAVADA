package com.uzlov.dating.lavada.app

class Constants {
    companion object {

        const val FIREBASE_PATH_USERS = "users"
        const val FIREBASE_PATH_SUBSCRIPTIONS = "subscriptions"
        const val FIREBASE_PATH_PURCHASES = "purchases"
        const val FIREBASE_PATH_CHATS = "chats"
        const val FIREBASE_PATH_GIFTS = "gifts"

        // preferences
        const val LOCAL_PREF_USER_ID = "user_id"
        const val LOCAL_PREF_USER_NAME = "user_name"
        const val LOCAL_PREF_USER_DATETIME = "user_datetime"

        // pref filter
        const val LOCAL_PREF_FILTER_SEX = "FILTER_SEX"
        const val LOCAL_PREF_FILTER_AGE_START = "FILTER_AGE_START"
        const val LOCAL_PREF_FILTER_AGE_END = "FILTER_AGE_END"
        const val LOCAL_PREF_FILTER_LATITUDE = "FILTER_LATITUDE"
        const val LOCAL_PREF_FILTER_LONGITUDE = "FILTER_LONGITUDE"
    }
}