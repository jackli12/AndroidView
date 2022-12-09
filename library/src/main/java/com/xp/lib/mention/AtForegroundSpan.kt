package com.xp.lib.mention

import android.text.style.ForegroundColorSpan

class AtForegroundSpan(color: Int) : ForegroundColorSpan(color) {
    var userId: String? = null //传给服务器的id
    var username: String? = null//传给服务器的名字
//    var showContent: String? = null//显示在EditText上的名字
    var start: Int = 0 //@昵称起始位置
    var len: Int = 0 //@昵称长度

}