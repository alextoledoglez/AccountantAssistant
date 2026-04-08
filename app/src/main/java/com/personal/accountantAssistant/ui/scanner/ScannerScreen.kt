package com.personal.accountantAssistant.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.ui.scanner.parsers.BillParser
import com.personal.accountantAssistant.ui.scanner.parsers.CardParser
import com.personal.accountantAssistant.ui.scanner.parsers.BuyParser
import com.personal.accountantAssistant.ui.theme.Dimens
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun ScannerScreen(
    mode: ScanMode,
    onResult: (ScanResult) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) { onDispose { analysisExecutor.shutdown() } }

    val hasDetected = remember { AtomicBoolean(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(
                                    analysisExecutor,
                                    buildAnalyzer(mode, hasDetected, onResult)
                                )
                            }

                        runCatching {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.camera_permission_denied),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.spacingMd)
                )
            }
        }

        // Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = stringResource(mode.hintRes),
                    color = Color.White,
                    fontSize = Dimens.textMd,
                    modifier = Modifier.padding(Dimens.spacingSm)
                )
            }

            Button(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Text(
                    text = stringResource(R.string.scan_manual_entry),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun buildAnalyzer(
    mode: ScanMode,
    hasDetected: AtomicBoolean,
    onResult: (ScanResult) -> Unit
): ImageAnalysis.Analyzer = when (mode) {

    ScanMode.PRODUCT_BARCODE -> {
        val barcodeScanner = BarcodeScanning.getClient()
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        ImageAnalysis.Analyzer { proxy ->
            if (hasDetected.get()) {
                proxy.close(); return@Analyzer
            }
            val bitmap = proxy.toBitmap()
            val rotation = proxy.imageInfo.rotationDegrees
            proxy.close()
            val image = InputImage.fromBitmap(bitmap, rotation)
            barcodeScanner.process(image).addOnSuccessListener { barcodes ->
                val barcode = barcodes
                    .filter { it.format != Barcode.FORMAT_UNKNOWN }
                    .firstNotNullOfOrNull { it.rawValue }
                    ?: return@addOnSuccessListener
                // Barcode detected — also run OCR to extract price label data
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (hasDetected.compareAndSet(false, true)) {
                            onResult(BuyParser.parse(visionText.text))
                        }
                    }
                    .addOnFailureListener {
                        if (hasDetected.compareAndSet(false, true)) {
                            onResult(ScanResult.Product(name = barcode))
                        }
                    }
            }
        }
    }

    ScanMode.BILL_TEXT -> {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        ImageAnalysis.Analyzer { proxy ->
            if (hasDetected.get()) {
                proxy.close(); return@Analyzer
            }
            val bitmap = proxy.toBitmap()
            val rotation = proxy.imageInfo.rotationDegrees
            proxy.close()
            val image = InputImage.fromBitmap(bitmap, rotation)
            recognizer.process(image).addOnSuccessListener { visionText ->
                BillParser.parse(visionText.text)?.let { result ->
                    if (hasDetected.compareAndSet(false, true)) onResult(result)
                }
            }
        }
    }

    ScanMode.CARD_TEXT -> {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        ImageAnalysis.Analyzer { proxy ->
            if (hasDetected.get()) {
                proxy.close(); return@Analyzer
            }
            val bitmap = proxy.toBitmap()
            val rotation = proxy.imageInfo.rotationDegrees
            proxy.close()
            val image = InputImage.fromBitmap(bitmap, rotation)
            recognizer.process(image).addOnSuccessListener { visionText ->
                CardParser.parse(visionText.text)?.let { result ->
                    if (hasDetected.compareAndSet(false, true)) onResult(result)
                }
            }
        }
    }
}