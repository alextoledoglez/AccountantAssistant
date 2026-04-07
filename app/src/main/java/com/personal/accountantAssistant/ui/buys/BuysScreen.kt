package com.personal.accountantAssistant.ui.buys

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen

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

    val filteredBuys = remember(buys, searchQuery) {
        if (searchQuery.isBlank())
            buys.orEmpty()
        else
            buys?.filter { it.name.containStr(searchQuery) }.orEmpty()
    }

    ExpensesListScreen(
        titleRes = com.personal.accountantAssistant.R.string.menu_buys,
        isLoading = isLoading,
        flipper = flipper,
        items = filteredBuys,
        summary = summary,
        searchQuery = searchQuery,
        onSearch = { searchQuery = it },
        onToggleAll = { viewModel.setAllBuysActive(it) },
        onRefresh = { viewModel.loadBuys() },
        onEdit = onEdit,
        onActive = onActive,
        onDelete = onDelete,
        showDate = false
    )
}