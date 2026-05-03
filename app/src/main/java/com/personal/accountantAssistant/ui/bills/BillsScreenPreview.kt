package com.personal.accountantAssistant.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.common.PrimaryFabButton
import com.personal.accountantAssistant.ui.common.SmallPrimaryFabButton
import com.personal.accountantAssistant.ui.expenses.ExpensesListScreen
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.theme.Dimens
import java.math.BigDecimal
import java.util.Calendar

private fun dateOf(year: Int, month: Int, day: Int) =
    Calendar.getInstance().apply { set(year, month - 1, day) }.time

@Composable
internal fun BillsPreviewContent() {
    val mockBills = listOf(
        ExpenseModel(
            id = 1,
            name = "RENT",
            quantity = 1,
            date = dateOf(2024, 10, 9),
            unitaryValue = BigDecimal("2000.00"),
            isActive = true,
            type = ExpensesType.BILL
        ),
        ExpenseModel(
            id = 2,
            name = "TELEPHONE",
            quantity = 1,
            date = dateOf(2024, 10, 11),
            unitaryValue = BigDecimal("50.00"),
            isActive = false,
            type = ExpensesType.BILL
        ),
        ExpenseModel(
            id = 3,
            name = "ELECTRICITY",
            quantity = 1,
            date = dateOf(2024, 10, 13),
            unitaryValue = BigDecimal("1100.00"),
            isActive = true,
            type = ExpensesType.BILL
        ),
        ExpenseModel(
            id = 4,
            name = "WATER",
            quantity = 1,
            date = dateOf(2024, 10, 14),
            unitaryValue = BigDecimal("350.00"),
            isActive = true,
            type = ExpensesType.BILL
        )
    )
    val summary = SummaryModel(activeCount = 3, total = BigDecimal("3450.00"))

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = TabPositions.BILLS,
                hasDeleteAllAction = true,
                onDeleteAllClick = {}
            )
        },
        bottomBar = { MainBottomBar(currentTab = TabPositions.BILLS, onTabSelected = {}) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                SmallPrimaryFabButton(
                    isVisible = true,
                    painterResourceId = R.drawable.ic_scan_white,
                    stringResourceId = R.string.scan_action,
                    onClick = {}
                )
                PrimaryFabButton(
                    isVisible = true,
                    painterResourceId = R.drawable.ic_add_white,
                    stringResourceId = R.string.add_action,
                    onClick = {}
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ListSummaryCard(
                summary = summary,
                itemCount = mockBills.size,
                searchQuery = "",
                onSearch = {},
                onToggleAll = {}
            )
            ExpensesListScreen(
                modifier = Modifier.weight(1f),
                isLoading = false,
                flipper = null,
                items = mockBills,
                onRefresh = {},
                onEdit = {},
                onActive = {},
                onDelete = {},
                showDate = true
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BillsScreenPreview() {
    AccountantTheme { BillsPreviewContent() }
}