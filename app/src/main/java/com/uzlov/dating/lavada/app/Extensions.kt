package com.uzlov.dating.lavada.app

import android.content.Context

fun Context.dpTpPx(dp:Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}