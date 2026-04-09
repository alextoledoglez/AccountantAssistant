package com.personal.accountantAssistant.ui.scanner.camera

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
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
import com.personal.accountantAssistant.ui.scanner.parsers.CardParser
import com.personal.accountantAssistant.ui.scanner.parsers.ScanParsingProfiles
import java.util.concurrent.atomic.AtomicBoolean

object CameraImageAnalyzer {

    @OptIn(ExperimentalGetImage::class)
    fun buildAnalyzer(
        context: Context,
        mode: ScanMode,
        configuration: Configuration,
        hasDetected: AtomicBoolean,
        onResult: (ScanResult) -> Unit
    ): ImageAnalysis.Analyzer {

        val isBusy = AtomicBoolean(false)
        val barcodeScanner = BarcodeScanning.getClient()
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val language = configuration.locales[0]?.language
        val country = configuration.locales[0]?.country
        val localeProfile = ScanParsingProfiles.defaultFor(language, country)

        // Pre-build parsers for all known profiles so content-based detection is allocation-free per frame
        val buyParsers = mapOf(
            ScanParsingProfiles.Brazil to BuyParser(ScanParsingProfiles.Brazil),
            ScanParsingProfiles.Spain to BuyParser(ScanParsingProfiles.Spain),
            ScanParsingProfiles.Usa to BuyParser(ScanParsingProfiles.Usa)
        )
        val billParsers = mapOf(
            ScanParsingProfiles.Brazil to BillParser(ScanParsingProfiles.Brazil),
            ScanParsingProfiles.Spain to BillParser(ScanParsingProfiles.Spain),
            ScanParsingProfiles.Usa to BillParser(ScanParsingProfiles.Usa)
        )

        val buyParser = buyParsers[localeProfile] ?: BuyParser(localeProfile)
        val billParser = billParsers[localeProfile] ?: BillParser(localeProfile)

        val buyAggregator = BuyConsensusAggregator(requiredHits = 2)
        val billAggregator = BillConsensusAggregator(requiredHits = 2)

        var productFrameCounter = 0

        return ImageAnalysis.Analyzer { proxy ->
            if (hasDetected.get()) {
                proxy.close()
                return@Analyzer
            }

            val mediaImage = proxy.image
            if (mediaImage == null) {
                proxy.close()
                return@Analyzer
            }

            if (!isBusy.compareAndSet(false, true)) {
                proxy.close()
                return@Analyzer
            }

            val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)

            when (mode) {
                ScanMode.PRODUCT_BARCODE -> {
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
                                        buyParsers[ScanParsingProfiles.detectFrom(ocrText)]
                                            ?: buyParser
                                    activeBuyParser.parse(text = ocrText, barcode = barcode)
                                }
                            }
                        }
                        .addOnSuccessListener { candidate ->
                            val minimumData = candidate.barcode.isNotBlank() ||
                                    candidate.price.isNotBlank() ||
                                    candidate.name.isNotBlank()

                            if (!minimumData) return@addOnSuccessListener

                            val requiredHits = when {
                                candidate.barcode.isNotBlank() && candidate.confidence >= 0.45f -> 1
                                candidate.barcode.isNotBlank() -> 2
                                else -> 2
                            }

                            val stable =
                                if (requiredHits == 1) candidate else buyAggregator.offer(candidate)

                            if (stable != null && hasDetected.compareAndSet(false, true)) {
                                onResult(stable)
                            }
                        }
                        .addOnCompleteListener {
                            isBusy.set(false)
                            proxy.close()
                        }
                }

                ScanMode.BILL_TEXT -> {
                    textRecognizer.process(image)
                        .addOnSuccessListener { vision ->
                            val text = vision.text
                            val activeBillParser =
                                billParsers[ScanParsingProfiles.detectFrom(text)] ?: billParser
                            val candidate =
                                activeBillParser.parse(text) ?: return@addOnSuccessListener

                            val requiredHits = when {
                                candidate.value.isNotBlank() &&
                                        candidate.date.isNotBlank() &&
                                        candidate.confidence >= 0.80f -> 1

                                else -> 2
                            }

                            val stable =
                                if (requiredHits == 1) candidate else billAggregator.offer(candidate)
                            if (stable != null && hasDetected.compareAndSet(false, true)) {
                                onResult(stable)
                            }
                        }
                        .addOnCompleteListener {
                            isBusy.set(false)
                            proxy.close()
                        }
                }

                ScanMode.CARD_TEXT -> {
                    textRecognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            CardParser.parse(visionText.text)?.let { result ->
                                if (hasDetected.compareAndSet(false, true)) {
                                    onResult(result)
                                }
                            }
                        }
                        .addOnCompleteListener {
                            isBusy.set(false)
                            proxy.close()
                        }
                }
            }
        }
    }
}