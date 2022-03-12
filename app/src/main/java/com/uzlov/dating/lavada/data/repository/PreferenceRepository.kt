package com.uzlov.dating.lavada.data.repository

import android.content.SharedPreferences
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.app.Constants.Companion.LOCAL_PREF_PREMIUM_VISIBLE
import com.uzlov.dating.lavada.app.Constants.Companion.LOCAL_PREF_THEME
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import com.uzlov.dating.lavada.domain.models.NotificationsFilter
import com.uzlov.dating.lavada.domain.models.UserFilter
import javax.inject.Inject

class PreferenceRepository @Inject constructor(private var preferences: SharedPreferences) {

    fun readUser() : AuthorizedUser? {
        return if (preferences.contains(Constants.LOCAL_PREF_USER_ID) &&
            preferences.contains(Constants.LOCAL_PREF_USER_NAME) &&
            preferences.contains(Constants.LOCAL_PREF_USER_DATETIME)){
            AuthorizedUser(
                uuid = preferences.getString(Constants.LOCAL_PREF_USER_ID, "") ?: "",
                name = preferences.getString(Constants.LOCAL_PREF_USER_NAME, "") ?: "",
                datetime = preferences.getLong(Constants.LOCAL_PREF_USER_DATETIME, 0),
                isReady = preferences.getBoolean(Constants.LOCAL_PREF_USER_IS_READY, false)
            )
        } else {
            null
        }
    }

    fun updateUser(profile: AuthorizedUser) {
        preferences.edit().apply {
            putString(Constants.LOCAL_PREF_USER_ID, profile.uuid)
            putString(Constants.LOCAL_PREF_USER_NAME, profile.name)
            putLong(Constants.LOCAL_PREF_USER_DATETIME, profile.datetime)
            putBoolean(Constants.LOCAL_PREF_USER_IS_READY, profile.isReady)
        }.apply()
    }

    fun setFilter(filter: UserFilter){
        preferences.edit().apply {
            putInt(Constants.LOCAL_PREF_FILTER_SEX, filter.sex)
            putInt(Constants.LOCAL_PREF_FILTER_AGE_START, filter.ageStart)
            putInt(Constants.LOCAL_PREF_FILTER_AGE_END, filter.ageEnd)
            putFloat(Constants.LOCAL_PREF_FILTER_LATITUDE, filter.latitude)
            putFloat(Constants.LOCAL_PREF_FILTER_LONGITUDE, filter.longitude)
        }.apply()
    }

    fun readFilter(): UserFilter {
        return UserFilter(
             preferences.getInt(Constants.LOCAL_PREF_FILTER_SEX, 0),
             preferences.getInt(Constants.LOCAL_PREF_FILTER_AGE_START, 18),
             preferences.getInt(Constants.LOCAL_PREF_FILTER_AGE_END, 50),
             preferences.getFloat(Constants.LOCAL_PREF_FILTER_LATITUDE, 0.0F),
             preferences.getFloat(Constants.LOCAL_PREF_FILTER_LONGITUDE, 0.0F),
        )
    }

    fun clearUser() {
        preferences.edit().apply {
            putString(Constants.LOCAL_PREF_USER_ID, "")
            putString(Constants.LOCAL_PREF_USER_NAME, "")
            putLong(Constants.LOCAL_PREF_USER_DATETIME, -1L)
            putBoolean(Constants.LOCAL_PREF_USER_IS_READY, false)
        }.apply()
    }

    fun updateNotificationsFilter(filter: NotificationsFilter){
        preferences.edit().apply {
            putBoolean(Constants.NOTIFICATION_FILER_MESSAGES, filter.messages)
            putBoolean(Constants.NOTIFICATION_FILER_MATCHES, filter.matches)
            putBoolean(Constants.NOTIFICATION_FILER_LIKES, filter.likes)
            putBoolean(Constants.NOTIFICATION_FILER_WATCHING_VIDEO, filter.watchingVideo)
            putBoolean(Constants.NOTIFICATION_FILER_GIFTS, filter.gifts)
            putBoolean(Constants.NOTIFICATION_FILER_NEWS, filter.news)
        }.apply()
    }
    fun readNotificationsFilter(): NotificationsFilter{
        return NotificationsFilter(
            preferences.getBoolean(Constants.NOTIFICATION_FILER_MESSAGES, true),
            preferences.getBoolean(Constants.NOTIFICATION_FILER_MATCHES, true),
            preferences.getBoolean(Constants.NOTIFICATION_FILER_LIKES, true),
            preferences.getBoolean(Constants.NOTIFICATION_FILER_WATCHING_VIDEO, true),
            preferences.getBoolean(Constants.NOTIFICATION_FILER_GIFTS, true),
            preferences.getBoolean(Constants.NOTIFICATION_FILER_NEWS, true),

        )
    }

    fun setTheme(theme: Boolean){
        preferences.edit().apply{
            putBoolean(LOCAL_PREF_THEME, theme)
        }.apply()
    }

    fun readTheme(): Boolean{
        return preferences.getBoolean(LOCAL_PREF_THEME, false)
    }

    fun setPremiumVisible(premium: Boolean){
        preferences.edit().apply{
            putBoolean(LOCAL_PREF_PREMIUM_VISIBLE, premium)
        }.apply()
    }
    fun readPremiumVisible(): Boolean{
        return preferences.getBoolean(LOCAL_PREF_PREMIUM_VISIBLE, false)
    }
}