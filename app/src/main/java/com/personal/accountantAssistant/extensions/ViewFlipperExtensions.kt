package com.personal.accountantAssistant.extensions

import android.widget.ViewFlipper

fun ViewFlipper.updateDisplayedChild(child: Int) {
    if (displayedChild != child)
        displayedChild = child
}