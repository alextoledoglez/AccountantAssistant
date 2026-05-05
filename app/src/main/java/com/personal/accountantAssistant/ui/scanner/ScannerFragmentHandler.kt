package com.personal.accountantAssistant.ui.scanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.personal.accountantAssistant.extensions.getParcelableExtraCompat

fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchScannerActivity(
    context: Context?,
    scanMode: ScanMode?
) {
    if (context == null) {
        return
    }
    if (scanMode == null) {
        return
    }
    try {
        this.launch(input = ScannerActivity.newIntent(context, scanMode))
    } catch (e: Exception) {
        Log.i("ScannerActivity", "Exception trying to launch with message: ${e.message}")
    }
}

fun ActivityResult.onScannerActivityResult(onScannerResult: (scanResult: ScanResult?) -> Unit) {
    val scanResult = if (resultCode == Activity.RESULT_OK) {
        data?.getParcelableExtraCompat<ScanResult>(ScannerActivity.EXTRA_RESULT)
    } else {
        null
    }
    onScannerResult(scanResult)
}