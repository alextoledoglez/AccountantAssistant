package com.personal.accountantAssistant.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

fun ViewParent.asView() = this as? View
fun ViewParent.asViewGroup() = this as? ViewGroup