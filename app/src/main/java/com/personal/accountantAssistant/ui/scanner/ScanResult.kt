package com.personal.accountantAssistant.ui.scanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ScanResult : Parcelable {

    @Parcelize
    data class Buy(
        val barcode: String = "",
        val name: String = "",
        val price: String = "",
        val confidence: Float = 0f,
        val rawText: String = ""
    ) : ScanResult()

    @Parcelize
    data class Bill(
        val name: String = "",
        val value: String = "",
        val date: String = "",
        val confidence: Float = 0f,
        val rawText: String = ""
    ) : ScanResult()

    @Parcelize
    data class Card(
        val company: String,
        val lastDigits: String,
        val expiry: String
    ) : ScanResult()
}