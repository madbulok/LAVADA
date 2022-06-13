package com.uzlov.dating.lavada.app

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.domain.models.Chat

fun Context.dpTpPx(dp:Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}

fun TextView.insertLink(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(text)
    var startIndexOfLink = -1

    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = resources.getColor(R.color.Link_text)
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun Chat.getCompanionUid(selfUid: String) : String {
    if (members.size < 2) return ""
    return if (members[0] == selfUid) members[1] else members[0]
}

object Extensions {
    private val linkedPicturesCoins = mapOf<String, Int>(
        "lavcoins_300" to R.drawable.ic_lavcoins_box_300,
        "lavcoins_500" to R.drawable.ic_lavcoins_box_500,
        "lavcoins_1500" to R.drawable.ic_lavcoins_box_1500,
        "lavcoins_3500" to R.drawable.ic_lavcoins_box_3500,
        "lavcoins_5000" to R.drawable.ic_lavcoins_superbox_5000,
    )

    private val linkedSaleCoins = mapOf<String, String>(
        "lavcoins_300" to "-15%",
        "lavcoins_500" to "-20%",
        "lavcoins_1500" to "-35%",
        "lavcoins_3500" to "-50%",
        "lavcoins_5000" to "",
    )

    fun getPurchasesId() = linkedPicturesCoins.keys.toList()
    fun getPictureForPurchase(purchaseId: String) = linkedPicturesCoins[purchaseId]
    fun getSaleForPurchase(purchaseId: String) = linkedSaleCoins[purchaseId]
}