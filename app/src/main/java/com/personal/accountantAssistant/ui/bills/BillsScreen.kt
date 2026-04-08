package com.personal.accountantAssistant.ui.bills

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsFragment
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillsScreen() {
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

    LaunchedEffect(Unit) { viewModel.loadBills() }

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
                fragmentManager?.let {
                    ExpenseDetailsFragment.showDialogFragment(
                        model = model,
                        onEdit = viewModel::saveBill,
                        manager = it
                    )
                }
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