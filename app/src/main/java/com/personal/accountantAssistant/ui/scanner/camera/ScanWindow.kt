package com.personal.accountantAssistant.ui.scanner.camera

import androidx.compose.ui.geometry.Rect
import com.personal.accountantAssistant.ui.scanner.ScanCodeType

data class ScanWindow(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    fun toRect(): Rect = Rect(left, top, right, bottom)

    companion object {
        fun calculateScanWindow(type: ScanCodeType, width: Float, height: Float): ScanWindow {
            return when (type) {
                ScanCodeType.QR_CODE -> {
                    val boxSize = width * 0.64f
                    val left = (width - boxSize) / 2f
                    val top = (height - boxSize) / 2f
                    ScanWindow(left, top, left + boxSize, top + boxSize)
                }

                ScanCodeType.BARCODE -> {
                    val boxWidth = width * 0.32f
                    val boxHeight = height * 0.54f
                    val left = (width - boxWidth) / 2f
                    val top = (height - boxHeight) / 2f
                    ScanWindow(left, top, left + boxWidth, top + boxHeight)
                }
            }
        }
    }
}