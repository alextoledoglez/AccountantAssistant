package com.personal.accountantAssistant.ui.scanner.camera

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.personal.accountantAssistant.ui.scanner.ScanMode
import com.personal.accountantAssistant.ui.scanner.ScanResult
import com.personal.accountantAssistant.ui.scanner.parsers.BillConsensusAggregator
import com.personal.accountantAssistant.ui.scanner.parsers.BillParser
import com.personal.accountantAssistant.ui.scanner.parsers.BuyConsensusAggregator
import com.personal.accountantAssistant.ui.scanner.parsers.BuyParser
import com.personal.accountantAssistant.ui.scanner.parsers.ScanParsingProfiles
import java.util.concurrent.atomic.AtomicBoolean

class CameraImageAnalyzer(
    private val mode: ScanMode,
    configuration: Configuration,
    private val hasDetected: AtomicBoolean,
    private val onResult: (ScanResult) -> Unit
) : ImageAnalysis.Analyzer {

    private val isBusy = AtomicBoolean(false)
    private val barcodeScanner = BarcodeScanning.getClient()
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val localeProfile = ScanParsingProfiles.defaultFor(
        configuration.locales[0]?.language,
        configuration.locales[0]?.country
    )

    // Pre-build parsers for all known profiles so content-based detection is allocation-free per frame
    private val buyParsers = mapOf(
        ScanParsingProfiles.Brazil to BuyParser(ScanParsingProfiles.Brazil),
        ScanParsingProfiles.Spain to BuyParser(ScanParsingProfiles.Spain),
        ScanParsingProfiles.Usa to BuyParser(ScanParsingProfiles.Usa)
    )
    private val billParsers = mapOf(
        ScanParsingProfiles.Brazil to BillParser(ScanParsingProfiles.Brazil),
        ScanParsingProfiles.Spain to BillParser(ScanParsingProfiles.Spain),
        ScanParsingProfiles.Usa to BillParser(ScanParsingProfiles.Usa)
    )

    private val buyParser = buyParsers[localeProfile] ?: BuyParser(localeProfile)
    private val billParser = billParsers[localeProfile] ?: BillParser(localeProfile)

    private val buyAggregator = BuyConsensusAggregator(requiredHits = 2)
    private val billAggregator = BillConsensusAggregator(requiredHits = 2)

    private var productFrameCounter = 0

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(proxy: ImageProxy) {
        if (hasDetected.get()) {
            proxy.close(); return
        }
        val mediaImage = proxy.image ?: run { proxy.close(); return }
        if (!isBusy.compareAndSet(false, true)) {
            proxy.close(); return
        }

        val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)

        when (mode) {
            ScanMode.PRODUCT_BARCODE -> analyzeBarcode(image, proxy)
            ScanMode.BILL_TEXT -> analyzeBill(image, proxy)
        }
    }

    private fun analyzeBarcode(image: InputImage, proxy: ImageProxy) {
        productFrameCounter++
        barcodeScanner.process(image)
            .continueWithTask { barcodeTask ->
                val barcode = barcodeTask.result
                    ?.filter { it.format != Barcode.FORMAT_UNKNOWN }
                    ?.firstNotNullOfOrNull {
                        it.rawValue?.trim()?.takeIf(String::isNotBlank)
                    }
                    .orEmpty()

                val shouldRunOcr = barcode.isBlank() || productFrameCounter % 3 == 0

                if (!shouldRunOcr) {
                    Tasks.forResult(
                        ScanResult.Buy(
                            barcode = barcode,
                            confidence = 0.45f,
                            rawText = ""
                        )
                    )
                } else {
                    textRecognizer.process(image).continueWith { textTask ->
                        val ocrText = textTask.result?.text.orEmpty()
                        val activeBuyParser =
                            buyParsers[ScanParsingProfiles.detectFrom(ocrText)] ?: buyParser
                        activeBuyParser.parse(text = ocrText, barcode = barcode)
                    }
                }
            }
            .addOnSuccessListener { candidate ->
                val minimumData = candidate.barcode.isNotBlank() ||
                        candidate.price.isNotBlank() ||
                        candidate.name.isNotBlank()

                if (!minimumData) return@addOnSuccessListener

                val requiredHits =
                    if (candidate.barcode.isNotBlank() && candidate.confidence >= 0.45f) 1 else 2
                val stable = if (requiredHits == 1) candidate else buyAggregator.offer(candidate)

                if (stable != null && hasDetected.compareAndSet(false, true)) {
                    onResult(stable)
                }
            }
            .addOnCompleteListener {
                isBusy.set(false)
                proxy.close()
            }
    }

    private fun analyzeBill(image: InputImage, proxy: ImageProxy) {
        textRecognizer.process(image)
            .addOnSuccessListener { vision ->
                val text = vision.text
                val activeBillParser =
                    billParsers[ScanParsingProfiles.detectFrom(text)] ?: billParser
                val candidate = activeBillParser.parse(text) ?: return@addOnSuccessListener

                val requiredHits = if (
                    candidate.value.isNotBlank() &&
                    candidate.date.isNotBlank() &&
                    candidate.confidence >= 0.80f
                ) 1 else 2

                val stable = if (requiredHits == 1) candidate else billAggregator.offer(candidate)
                if (stable != null && hasDetected.compareAndSet(false, true)) {
                    onResult(stable)
                }
            }
            .addOnCompleteListener {
                isBusy.set(false)
                proxy.close()
            }
    }
}