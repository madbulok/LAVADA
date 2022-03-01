package com.uzlov.dating.lavada.data.repository

import android.content.SharedPreferences
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import com.uzlov.dating.lavada.domain.models.MALE
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
             preferences.getInt(Constants.LOCAL_PREF_FILTER_AGE_START, 0),
             preferences.getInt(Constants.LOCAL_PREF_FILTER_AGE_END, 0),
             preferences.getFloat(Constants.LOCAL_PREF_FILTER_LATITUDE, 0.0F),
             preferences.getFloat(Constants.LOCAL_PREF_FILTER_LONGITUDE, 0.0F),
        )
    }
}