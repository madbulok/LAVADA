package com.uzlov.dating.lavada.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ErrorUtils (
    val status: String? = null,
    val error: Err? = null
): Parcelable

@Parcelize
data class Err(
    val user_status: String? = null,
): Parcelable
