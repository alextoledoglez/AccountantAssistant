package com.personal.accountantAssistant.extensions

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.personal.accountantAssistant.R

fun AppCompatTextView.setup(
    @StringRes text: Int,
    @ColorRes color: Int = R.color.blackColor,
    @ColorRes backgroundColor: Int = R.color.whiteColor
) {
    setup(
        context.getString(text),
        context.getCompatColor(color),
        context.getCompatColor(backgroundColor)
    )
}

fun AppCompatTextView.setup(
    text: String,
    @ColorInt color: Int = context.getCompatColor(R.color.blackColor),
    @ColorInt backgroundColor: Int = context.getCompatColor(R.color.whiteColor)
) {
    this.text = text
    setTextColor(color)
    setBackgroundColor(backgroundColor)
}