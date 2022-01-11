package com.personal.accountantAssistant.domain.models.home

import android.graphics.Color

data class SummaryModel(
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
