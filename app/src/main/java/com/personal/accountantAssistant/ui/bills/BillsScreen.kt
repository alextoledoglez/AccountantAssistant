package com.personal.accountantAssistant.ui.bills

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen

@Composable
fun BillsScreen(
    viewModel: BillsViewModel,
    onEdit: (ExpenseModel) -> Unit,
    onActive: (ExpenseModel) -> Unit,
    onDelete: (ExpenseModel) -> Unit
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val flipper by viewModel.flipper.observeAsState()
    val bills by viewModel.bills.observeAsState(emptyList())
    val summary by viewModel.summary.observeAsState(SummaryModel())

    var searchQuery by remember { mutableStateOf("") }

    val filteredBills = remember(bills, searchQuery) {
        if (searchQuery.isBlank())
            bills.orEmpty()
        else
            bills?.filter { it.name.containStr(searchQuery) }.orEmpty()
    }

    ExpensesListScreen(
        titleRes = com.personal.accountantAssistant.R.string.menu_bills,
        isLoading = isLoading,
        flipper = flipper,
        items = filteredBills,
        summary = summary,
        searchQuery = searchQuery,
        onSearch = { searchQuery = it },
        onToggleAll = { viewModel.setAllBillsActive(it) },
        onRefresh = { viewModel.loadBills() },
        onEdit = onEdit,
        onActive = onActive,
        onDelete = onDelete,
        showDate = true
    )
}