package com.personal.accountantAssistant.ui.scanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ScanResult : Parcelable {

    /** Barcode/QR scan + optional OCR from price label */
    @Parcelize
    data class Product(
        val name: String = "",   // extracted from price label OCR
        val price: String = ""   // e.g. "12,99" — extracted from price label OCR
    ) : ScanResult()

    /** OCR from bill/invoice — raw strings extracted by regex */
    @Parcelize
    data class Bill(
        val name: String,
        val value: String,   // e.g. "123,45"
        val date: String     // e.g. "28/05/2025"
    ) : ScanResult()

    /** OCR from card face */
    @Parcelize
    data class Card(
        val company: String,
        val lastDigits: String,  // last 4 digits
        val expiry: String       // MM/YY
    ) : ScanResult()
}