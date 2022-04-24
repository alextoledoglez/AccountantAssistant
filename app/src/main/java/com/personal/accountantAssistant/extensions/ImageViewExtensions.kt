package com.personal.accountantAssistant.extensions

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

fun AppCompatImageView.setup(@DrawableRes icon: Int, @ColorRes color: Int) {
    setupImageWithColor(icon, context.getCompatColor(color))
}

fun AppCompatImageView.setupImageWithColor(@DrawableRes icon: Int, @ColorInt color: Int) {
    setImageResource(icon)
    setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
}