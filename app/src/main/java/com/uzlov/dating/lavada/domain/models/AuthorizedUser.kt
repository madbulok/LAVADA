package com.uzlov.dating.lavada.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthorizedUser (
    val uuid: String,
    val datetime: Long,
    val name: String,
    val isReady: Boolean = false
) : Parcelable