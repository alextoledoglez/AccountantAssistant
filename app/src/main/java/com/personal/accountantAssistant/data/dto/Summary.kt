package com.personal.accountantAssistant.data.dto

import android.graphics.Color

data class Summary(
    val available: Float?,
    val availableStr: String?,
    var availableColor: Int? = Color.GREEN,
    val total: Float?,
    val totalStr: String?,
    var totalColor: Int? = Color.GREEN,
    var needed: Float?,
    var neededStr: String?,
    var neededColor: Int? = Color.RED,
)
