package com.xp.lib.mention

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText

/**
 * 好友@自定义view
 */
class MentionEditText : AppCompatEditText {
    private val mentionList: MutableList<AtForegroundSpan> = ArrayList()
    private var mMentionTextColor = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val inputConnection = super.onCreateInputConnection(outAttrs)
        if (inputConnection != null) {
            outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        }
        return inputConnection
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        updateSpans()
    }

    fun addMention(userInfo: MentionInfo) {
        if (!containMention(userInfo.userid)) {
            val editable = text
            val start = selectionStart
            val name = "@" + userInfo.username
            val end = start + name.length
            val foregroundSpan = AtForegroundSpan(mMentionTextColor)
            foregroundSpan.userId = userInfo.userid
            foregroundSpan.username = name
            foregroundSpan.start = start
            foregroundSpan.len = name.length
            val styleSpan = AtStyleSpan(Typeface.BOLD)
            styleSpan.username = name
            styleSpan.start = start
            styleSpan.len = name.length
            if (editable != null) {
                editable.insert(start, name)
                editable.setSpan(foregroundSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                editable.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                editable.append(" ")
            }
            mentionList.add(foregroundSpan)
        } else {
            Toast.makeText(context,"You have mentioned this user",Toast.LENGTH_LONG).show()
        }
    }

    fun setTitleWithMention(title: String, mentions: List<MentionInfo>) {
        if (mentions.isNotEmpty()) {
            val spanString = SpannableStringBuilder(title)
            mentions.forEach { mentionInfo ->
                val start = mentionInfo.start ?: 0
                val len = mentionInfo.len ?: 0
                val end = start + len
                val foregroundSpan = AtForegroundSpan(mMentionTextColor)
                foregroundSpan.userId = mentionInfo.userid
                foregroundSpan.username = mentionInfo.username
                foregroundSpan.start = start
                foregroundSpan.len = len
                val styleSpan = AtStyleSpan(Typeface.BOLD)
                styleSpan.username = mentionInfo.username
                styleSpan.start = start
                styleSpan.len = len
                spanString.setSpan(foregroundSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spanString.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mentionList.add(foregroundSpan)
            }
            text = spanString

        } else {
            setText(title)
        }
    }

    private fun containMention(userId: String?): Boolean {
        val mention = mentionList.find { it.userId == userId }
        return mention != null
    }

    /**
     * set highlight color of mention string
     *
     * @param color value from 'getResources().getColor()' or 'Color.parseColor()' etc.
     */
    fun setMentionTextColor(color: Int) {
        mMentionTextColor = color
    }

    private fun init() {
        mMentionTextColor = Color.RED
    }

    private fun updateSpans() {
        val originContent = text
        if (originContent == null || TextUtils.isEmpty(originContent.toString())) {
            return
        }

        val atForegroundSpan =
            originContent.getSpans(0, originContent.length, AtForegroundSpan::class.java)
        val text = originContent.toString()
        for (foregroundSpan in atForegroundSpan) {
            //算出span在text中当前的起始位置，再获取之前设置span的文字的长度
//            val spanTextLen = foregroundSpan.len - foregroundSpan.start
            val spanStart = originContent.getSpanStart(foregroundSpan)

            //得到这一段区域的文字与span内的name对比
            var end = spanStart + foregroundSpan.len
            if (end >= text.length) {
                end = text.length
            }
            val currentName = text.substring(spanStart, end)
            if (TextUtils.equals(foregroundSpan.username, currentName)) {
                //更新span的start
                foregroundSpan.start = spanStart
            } else {
                originContent.removeSpan(foregroundSpan)
                mentionList.remove(foregroundSpan)
            }
        }


        val styleSpans = originContent.getSpans(
            0, originContent.length,
            AtStyleSpan::class.java
        )

        for (styleSpan in styleSpans) {
            val spanStart = originContent.getSpanStart(styleSpan)
            var end = spanStart + styleSpan.len
            if (end >= text.length) {
                end = text.length
            }
            val currentName = text.substring(spanStart, end)
            if (!TextUtils.equals(styleSpan.username, currentName)) {
                originContent.removeSpan(styleSpan)
            }
        }

    }

    fun getMentionList(): MutableList<AtForegroundSpan> {
        return mentionList
    }

}