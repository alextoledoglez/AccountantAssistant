package com.personal.accountantAssistant.ui.scanner.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.personal.accountantAssistant.ui.scanner.ScanCodeType
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

@OptIn(ExperimentalGetImage::class)
internal class ImageScanAnalyzer(
    private val type: ScanCodeType,
    private val scanner: BarcodeScanner,
    private val previewWidthProvider: () -> Float,
    private val previewHeightProvider: () -> Float,
    private val onCandidateDetected: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    private val isBusy = AtomicBoolean(false)

    override fun analyze(proxy: ImageProxy) {
        val mediaImage = proxy.image
        if (mediaImage == null) {
            proxy.close()
            return
        }

        if (!isBusy.compareAndSet(false, true)) {
            proxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val previewWidth = previewWidthProvider()
                val previewHeight = previewHeightProvider()

                if (previewWidth <= 0f || previewHeight <= 0f) return@addOnSuccessListener

                val scanWindow = ScanWindow.calculateScanWindow(type, previewWidth, previewHeight)

                val bestCandidate = barcodes
                    .asSequence()
                    .filter { matchesMode(type, barcode = it) }
                    .filter { it.rawValue?.isNotBlank() == true }
                    .filter { isInsideScanWindow(it, scanWindow) }
                    .maxByOrNull { scoreBarcode(it, scanWindow) }

                bestCandidate?.let(onCandidateDetected)
            }
            .addOnCompleteListener {
                isBusy.set(false)
                proxy.close()
            }
    }

    private fun matchesMode(
        type: ScanCodeType,
        barcode: Barcode
    ): Boolean {
        return when (type) {
            ScanCodeType.QR_CODE -> barcode.format == Barcode.FORMAT_QR_CODE
            ScanCodeType.BARCODE -> barcode.format != Barcode.FORMAT_QR_CODE
        }
    }

    private fun isInsideScanWindow(
        barcode: Barcode,
        scanWindow: ScanWindow
    ): Boolean {
        val box = barcode.boundingBox ?: return false
        val centerX = box.exactCenterX()
        val centerY = box.exactCenterY()

        return centerX in scanWindow.left..scanWindow.right &&
                centerY in scanWindow.top..scanWindow.bottom
    }

    private fun scoreBarcode(
        barcode: Barcode,
        scanWindow: ScanWindow
    ): Int {
        val box = barcode.boundingBox ?: return 0

        val centerX = box.exactCenterX()
        val centerY = box.exactCenterY()

        val scanCenterX = (scanWindow.left + scanWindow.right) / 2f
        val scanCenterY = (scanWindow.top + scanWindow.bottom) / 2f

        val distancePenalty = (
                abs(centerX - scanCenterX) + abs(centerY - scanCenterY)
                ).toInt()

        val sizeScore = box.width() * box.height()

        return sizeScore - distancePenalty
    }
}