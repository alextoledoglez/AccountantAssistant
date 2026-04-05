package com.personal.accountantAssistant.extensions

import android.view.View
import android.view.ViewGroup

fun View.setLeftPadding(leftPaddingPadding: Int) {
    setPadding(leftPaddingPadding, paddingTop, paddingRight, paddingBottom)
}

fun View.setTopPadding(topPadding: Int) {
    setPadding(paddingLeft, topPadding, paddingRight, paddingBottom)
}

fun View.setRightPadding(rightPadding: Int) {
    setPadding(paddingLeft, paddingTop, rightPadding, paddingBottom)
}

fun View.setBottomPadding(bottomPadding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, bottomPadding)
}

fun View.asMarginLayoutParams() = layoutParams as? ViewGroup.MarginLayoutParams
