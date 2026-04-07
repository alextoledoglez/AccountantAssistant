package com.personal.accountantAssistant.ui.buys

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen
import com.personal.accountantAssistant.ui.expenses.ListSummaryCard

@Composable
fun BuysScreen(
    viewModel: BuysViewModel,
    onEdit: (ExpenseModel) -> Unit,
    onActive: (ExpenseModel) -> Unit,
    onDelete: (ExpenseModel) -> Unit
) {
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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.backgroundColor))) {
        ListSummaryCard(
            summary = summary,
            itemCount = buys?.size.orZero(),
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
            onEdit = onEdit,
            onActive = onActive,
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
                    onDelete(model)
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