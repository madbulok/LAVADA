package com.uzlov.dating.lavada.data.repository

import android.content.SharedPreferences
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.domain.models.AuthorizedUser
import javax.inject.Inject

class PreferenceRepository @Inject constructor(private var preferences: SharedPreferences) {

    fun readUser() : AuthorizedUser? {
        return if (preferences.contains(Constants.LOCAL_PREF_USER_ID) &&
            preferences.contains(Constants.LOCAL_PREF_USER_NAME) &&
            preferences.contains(Constants.LOCAL_PREF_USER_DATETIME)){
            AuthorizedUser(
                uuid = preferences.getString(Constants.LOCAL_PREF_USER_ID, "") ?: "",
                name = preferences.getString(Constants.LOCAL_PREF_USER_NAME, "") ?: "",
                datetime = preferences.getLong(Constants.LOCAL_PREF_USER_DATETIME, 0)
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
        }.apply()
    }
}