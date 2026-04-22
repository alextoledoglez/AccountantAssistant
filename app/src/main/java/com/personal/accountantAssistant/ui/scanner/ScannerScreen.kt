package com.personal.accountantAssistant.ui.scanner

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.isCameraPermissionGranted
import com.personal.accountantAssistant.ui.common.CircleIconButton
import com.personal.accountantAssistant.ui.common.RoundedTextButton
import com.personal.accountantAssistant.ui.scanner.barcode.BarcodeScanningBuilder
import com.personal.accountantAssistant.ui.scanner.camera.ImageScanAnalyzer
import com.personal.accountantAssistant.ui.scanner.camera.buildCameraPreview
import com.personal.accountantAssistant.ui.scanner.parser.BarcodeParser
import com.personal.accountantAssistant.ui.theme.Dimens
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(context.isCameraPermissionGranted()) }
    var codeType by remember { mutableStateOf(ScanCodeType.QR_CODE) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember(codeType) { BarcodeScanningBuilder.buildScanner(codeType) }
    val hasDetected = remember { AtomicBoolean(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    DisposableEffect(codeType) {
        onDispose { scanner.close() }
    }

    DisposableEffect(Unit) {
        onDispose { analysisExecutor.shutdown() }
    }

    LaunchedEffect(codeType) { hasDetected.set(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            key(codeType) {
                AndroidView(
                    factory = { ctx ->
                        buildCameraPreview(
                            context = ctx,
                            setAnalyzer = { analysis, previewView ->
                                analysis.setAnalyzer(
                                    analysisExecutor,
                                    ImageScanAnalyzer(
                                        type = codeType,
                                        scanner = scanner,
                                        previewWidthProvider = { previewView.width.toFloat() },
                                        previewHeightProvider = { previewView.height.toFloat() }
                                    ) { barcode ->
                                        if (!hasDetected.compareAndSet(false, true))
                                            return@ImageScanAnalyzer
                                        scope.launch {
                                            BarcodeParser.parse(mode, barcode)
                                                ?.let { result -> onResult(result) }
                                                ?: hasDetected.set(false)
                                        }
                                    }
                                )
                            },
                            lifecycleOwner = lifecycleOwner
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            ScannerOverlay(modifier = Modifier.fillMaxSize(), type = codeType)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.camera_permission_denied),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.spacingMd)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(Dimens.spacingMd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        stringResourceId = R.string.back_action,
                        onClick = onSkip
                    )

                    RoundedTextButton(
                        isSelected = codeType.isQrCode(),
                        stringResourceId = ScanCodeType.QR_CODE.textRes,
                        onClick = { codeType = ScanCodeType.QR_CODE }
                    )

                    RoundedTextButton(
                        isSelected = codeType.isBarcode(),
                        stringResourceId = ScanCodeType.BARCODE.textRes,
                        onClick = { codeType = ScanCodeType.BARCODE }
                    )
                }

                Text(
                    text = stringResource(id = mode.hintRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.scan_manual_entry_text),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}