package com.personal.accountantAssistant.ui.buys

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
fun BuysScreen() {

    val fragmentManager = (LocalActivity.current as? FragmentActivity)?.supportFragmentManager
    val viewModel: BuysViewModel = koinViewModel()

    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val buys by viewModel.buys.observeAsState(emptyList())
    val summary by viewModel.summary.observeAsState(SummaryModel())

    var searchQuery by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<ExpenseModel?>(null) }
    val filteredBuys = remember(buys, searchQuery) {
        if (searchQuery.isBlank())
            buys.orEmpty()
        else
            buys?.filter { it.name.containStr(searchQuery) }.orEmpty()
    }

    LaunchedEffect(Unit) { viewModel.loadBuys() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ListSummaryCard(
            summary = summary,
            itemCount = filteredBuys.size.orZero(),
            searchQuery = searchQuery,
            onSearch = { searchQuery = it },
            onToggleAll = { viewModel.setAllBuysActive(it) }
        )
        ExpensesListScreen(
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            flipper = flipper,
            items = filteredBuys,
            onRefresh = { viewModel.loadBuys() },
            onEdit = { model ->
                fragmentManager?.let {
                    ExpenseDetailsFragment.showDialogFragment(
                        model = model,
                        onEdit = viewModel::saveBuy,
                        manager = it
                    )
                }
            },
            onActive = viewModel::switchActiveBuy,
            onDelete = { pendingDelete = it },
            showDate = false
        )
    }

    pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_record_title)) },
            text = { Text(stringResource(R.string.delete_record_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBuy(model)
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