package com.personal.accountantAssistant.ui.scanner

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.isCameraPermissionGranted
import com.personal.accountantAssistant.ui.scanner.camera.CameraImageAnalyzer
import com.personal.accountantAssistant.ui.scanner.camera.CameraPreview
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
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(context.isCameraPermissionGranted()) }

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val hasDetected = remember { AtomicBoolean(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose { analysisExecutor.shutdown() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    CameraPreview.buildPreview(
                        context = ctx,
                        setAnalyzer = { analysis ->
                            val analyzer = CameraImageAnalyzer.buildAnalyzer(
                                context = context,
                                mode = mode,
                                configuration = configuration,
                                hasDetected = hasDetected,
                                onResult = onResult
                            )
                            analysis.setAnalyzer(analysisExecutor, analyzer)
                        },
                        lifecycleOwner = lifecycleOwner
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
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
            ScannerOverlay(mode, onSkip)
        }
    }
}