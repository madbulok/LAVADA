package com.uzlov.dating.lavada.app

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
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
                textPaint.color = textPaint.linkColor
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