package com.personal.accountantAssistant.ui.scanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.data.mappers.toBuy
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.getParcelableExtraCompat
import com.personal.accountantAssistant.extensions.toCurrencyOrZeroBigDecimal
import com.personal.accountantAssistant.extensions.toDate
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.wallet.WalletDetailsFragment
import com.personal.accountantAssistant.ui.wallet.WalletViewModel

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