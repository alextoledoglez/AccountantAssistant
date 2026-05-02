package com.personal.accountantAssistant.ui.bills

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.mappers.toBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toCurrencyOrZeroBigDecimal
import com.personal.accountantAssistant.extensions.toDate
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.common.PrimaryFabButton
import com.personal.accountantAssistant.ui.common.SmallPrimaryFabButton
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen
import com.personal.accountantAssistant.ui.scanner.ScanMode
import com.personal.accountantAssistant.ui.scanner.launchScannerActivity
import com.personal.accountantAssistant.ui.scanner.onScannerActivityResult
import com.personal.accountantAssistant.ui.theme.Dimens
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillsScreen(
    onSetFab: ((@Composable () -> Unit)?) -> Unit = {},
    onSetDeleteAll: ((() -> Unit)?) -> Unit = {}
) {
    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val viewModel: BillsViewModel = koinViewModel()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val bills by viewModel.bills.observeAsState(emptyList())
    val summary by viewModel.summary.observeAsState(SummaryModel())

    var searchQuery by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<ExpenseModel?>(null) }
    val filteredBills = remember(bills, searchQuery) {
        if (searchQuery.isBlank())
            bills.orEmpty()
        else
            bills?.filter { it.name.containStr(searchQuery) }.orEmpty()
    }
    val scanLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.onScannerActivityResult { scanResult ->
                val bill = scanResult?.asBill()
                ExpenseDetailsFragment.showDialogFragment(
                    fragmentManager = fragmentManager,
                    model = ExpenseModel(
                        name = bill?.name,
                        date = bill?.date.toDate(),
                        unitaryValue = bill?.value?.toCurrencyOrZeroBigDecimal().orZero()
                    ).toBill(),
                    onEdit = viewModel::saveBill
                )
            }
        }

    LaunchedEffect(Unit) { viewModel.loadBills() }

    DisposableEffect(Unit) {
        onSetFab { BillsFabs(scanLauncher, fragmentManager, viewModel) }
        onSetDeleteAll(viewModel::deleteAllBills)
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ListSummaryCard(
            summary = summary,
            itemCount = filteredBills.size.orZero(),
            searchQuery = searchQuery,
            onSearch = { searchQuery = it },
            onToggleAll = { viewModel.setAllBillsActive(it) }
        )
        ExpensesListScreen(
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            flipper = flipper,
            items = filteredBills,
            onRefresh = { viewModel.loadBills() },
            onEdit = { model ->
                ExpenseDetailsFragment.showDialogFragment(
                    fragmentManager = fragmentManager,
                    model = model,
                    onEdit = viewModel::saveBill
                )
            },
            onActive = viewModel::switchActiveBill,
            onDelete = { pendingDelete = it },
            showDate = true
        )
    }

    pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_record_title)) },
            text = { Text(stringResource(R.string.delete_record_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBill(model)
                    pendingDelete = null
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
internal fun BillsFabs(
    scanLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    fragmentManager: FragmentManager?,
    viewModel: BillsViewModel
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SmallPrimaryFabButton(
            isVisible = true,
            painterResourceId = R.drawable.ic_scan_white,
            stringResourceId = R.string.scan_action,
            onClick = { scanLauncher.launchScannerActivity(context, ScanMode.BILL) }
        )
        PrimaryFabButton(
            isVisible = true,
            painterResourceId = R.drawable.ic_add_white,
            stringResourceId = R.string.add_action,
            onClick = {
                ExpenseDetailsFragment.showDialogFragment(
                    fragmentManager = fragmentManager,
                    model = ExpenseModel().toBill(),
                    onEdit = viewModel::saveBill
                )
            }
        )
    }
}