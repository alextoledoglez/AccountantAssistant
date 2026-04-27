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

        /**
         * Transforms the visual scan window (defined in screen/preview space) into the
         * image coordinate space that MLKit uses for bounding boxes.
         *
         * PreviewView uses FILL_CENTER by default: the image is scaled to fill the view
         * and cropped symmetrically. This function accounts for that crop offset so the
         * filter window exactly matches what the user sees on screen.
         *
         * @param imageWidth  effective image width after applying rotation (px)
         * @param imageHeight effective image height after applying rotation (px)
         * @param previewWidth  PreviewView width in screen pixels
         * @param previewHeight PreviewView height in screen pixels
         */
        fun calculateScanWindowInImageSpace(
            type: ScanCodeType,
            imageWidth: Float,
            imageHeight: Float,
            previewWidth: Float,
            previewHeight: Float
        ): ScanWindow {
            // FILL_CENTER: scale image uniformly to fill the view, then crop the excess.
            val scale = maxOf(previewWidth / imageWidth, previewHeight / imageHeight)
            // Image pixels cropped from each side due to FILL_CENTER.
            val cropX = (imageWidth - previewWidth / scale) / 2f
            val cropY = (imageHeight - previewHeight / scale) / 2f
            // Portion of the image that is actually visible on screen.
            val visibleWidth = imageWidth - 2f * cropX
            val visibleHeight = imageHeight - 2f * cropY

            return when (type) {
                ScanCodeType.QR_CODE -> {
                    val boxSize = visibleWidth * 0.64f
                    val left = cropX + (visibleWidth - boxSize) / 2f
                    val top = cropY + (visibleHeight - boxSize) / 2f
                    ScanWindow(left, top, left + boxSize, top + boxSize)
                }
                ScanCodeType.BARCODE -> {
                    val boxWidth = visibleWidth * 0.32f
                    val boxHeight = visibleHeight * 0.54f
                    val left = cropX + (visibleWidth - boxWidth) / 2f
                    val top = cropY + (visibleHeight - boxHeight) / 2f
                    ScanWindow(left, top, left + boxWidth, top + boxHeight)
                }
            }
        }
    }
}