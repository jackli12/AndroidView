package com.xp.lib.mention

import android.text.style.StyleSpan

class AtStyleSpan(style: Int) : StyleSpan(style) {
    var username: String? = null
    var start: Int = 0
    var len: Int = 0

}