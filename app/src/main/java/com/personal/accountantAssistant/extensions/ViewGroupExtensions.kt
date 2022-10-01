package com.personal.accountantAssistant.extensions

import android.view.LayoutInflater
import android.view.ViewGroup

fun ViewGroup.toLayoutInflater(): LayoutInflater = LayoutInflater.from(this.context)