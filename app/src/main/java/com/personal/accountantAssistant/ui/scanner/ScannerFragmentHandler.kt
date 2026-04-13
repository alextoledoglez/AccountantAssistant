package com.personal.accountantAssistant.ui.scanner

import android.app.Activity
import android.content.Context
import android.content.Intent
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

internal fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchScannerActivity(
    context: Context?,
    scanMode: ScanMode?
) {
    if (context == null) {
        return
    }
    if (scanMode == null) {
        return
    }
    this.launch(input = ScannerActivity.newIntent(context, scanMode))
}

fun ActivityResult.onScannerActivityResult(onScannerResult: (scanResult: ScanResult?) -> Unit) {
    val scanResult = if (resultCode == Activity.RESULT_OK) {
        data?.getParcelableExtraCompat<ScanResult>(ScannerActivity.EXTRA_RESULT)
    } else {
        null
    }
    onScannerResult(scanResult)
}

internal fun FragmentManager.onScannerActivityResult(
    currentTab: TabPositions,
    activityResult: ActivityResult,
    walletViewModel: WalletViewModel,
    buysViewModel: BuysViewModel,
    billsViewModel: BillsViewModel,
) {
    if (activityResult.resultCode == Activity.RESULT_OK) {
        val scanResult =
            activityResult.data?.getParcelableExtraCompat<ScanResult>(ScannerActivity.EXTRA_RESULT)
        when (scanResult) {
            is ScanResult.Buy -> ExpenseDetailsFragment.showDialogFragment(
                model = ExpenseModel(
                    name = scanResult.name,
                    quantity = 1,
                    unitaryValue = scanResult.price.toCurrencyOrZeroBigDecimal()
                ).toBuy(),
                onEdit = buysViewModel::saveBuy,
                manager = this
            )

            is ScanResult.Bill -> ExpenseDetailsFragment.showDialogFragment(
                model = ExpenseModel(
                    name = scanResult.name,
                    unitaryValue = scanResult.value.toCurrencyOrZeroBigDecimal(),
                    date = scanResult.date.toDate()
                ).toBill(),
                onEdit = billsViewModel::saveBill,
                manager = this
            )

            null -> showEmptyFormDialog(currentTab, walletViewModel, buysViewModel, billsViewModel)
        }
    } else {
        showEmptyFormDialog(currentTab, walletViewModel, buysViewModel, billsViewModel)
    }
}

internal fun FragmentManager.showEmptyFormDialog(
    tab: TabPositions,
    walletViewModel: WalletViewModel,
    buysViewModel: BuysViewModel,
    billsViewModel: BillsViewModel
) {
    when (tab) {
        TabPositions.WALLET -> WalletDetailsFragment.showDialogFragment(
            model = CardModel(),
            onEdit = walletViewModel::saveCard,
            manager = this
        )

        TabPositions.BUYS -> ExpenseDetailsFragment.showDialogFragment(
            model = ExpenseModel().toBuy(),
            onEdit = buysViewModel::saveBuy,
            manager = this
        )

        TabPositions.BILLS -> ExpenseDetailsFragment.showDialogFragment(
            model = ExpenseModel().toBill(),
            onEdit = billsViewModel::saveBill,
            manager = this
        )

        else -> {}
    }
}