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
        const val LOCAL_PREF_USER_IS_READY = "user_is_ready"

        // pref filter
        const val LOCAL_PREF_FILTER_SEX = "FILTER_SEX"
        const val LOCAL_PREF_FILTER_AGE_START = "FILTER_AGE_START"
        const val LOCAL_PREF_FILTER_AGE_END = "FILTER_AGE_END"
        const val LOCAL_PREF_FILTER_LATITUDE = "FILTER_LATITUDE"
        const val LOCAL_PREF_FILTER_LONGITUDE = "FILTER_LONGITUDE"

        //notification filter
        const val NOTIFICATION_FILER_MESSAGES = "FILTER_MESSAGES"
        const val NOTIFICATION_FILER_MATCHES = "FILTER_MATCHES"
        const val NOTIFICATION_FILER_LIKES = "FILTER_LIKES"
        const val NOTIFICATION_FILER_WATCHING_VIDEO = "FILTER_WATCHING_VIDEO"
        const val NOTIFICATION_FILER_GIFTS = "FILTER_GIFTS"
        const val NOTIFICATION_FILER_NEWS = "FILTER_NEWS"

        //theme
        const val LOCAL_PREF_THEME = "theme"

        //premium
        const val LOCAL_PREF_PREMIUM_VISIBLE = "PREMIUM_VISIBLE"

        //male
        const val MAN = "male"
        const val WOMAN = "female"
        const val ANOTHER = "another"



    }
}