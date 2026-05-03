package com.personal.accountantAssistant.ui.scanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ScanResult : Parcelable {
    @Parcelize
    data class Buy(val name: String = "", val amount: String = "") : ScanResult()

    fun asBuy(): Buy? = this as? Buy

    @Parcelize
    data class Bill(
        val name: String = "",
        val amount: String = "",
        val date: String = ""
    ) : ScanResult()

    fun asBill(): Bill? = this as? Bill
}