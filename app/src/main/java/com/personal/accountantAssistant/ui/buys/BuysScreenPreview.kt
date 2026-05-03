package com.personal.accountantAssistant.ui.buys

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
import androidx.compose.ui.tooling.preview.Devices
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

@Composable
internal fun BuysPreviewContent() {
    val mockBuys = listOf(
        ExpenseModel(
            id = 1,
            name = "EGGS",
            quantity = 1,
            unitaryValue = BigDecimal("22.90"),
            isActive = true,
            type = ExpensesType.BUY
        ),
        ExpenseModel(
            id = 2,
            name = "APPLES",
            quantity = 2,
            unitaryValue = BigDecimal("52.84"),
            isActive = true,
            type = ExpensesType.BUY
        ),
        ExpenseModel(
            id = 3,
            name = "PAPER TOWEL",
            quantity = 6,
            unitaryValue = BigDecimal("3.69"),
            isActive = true,
            type = ExpensesType.BUY
        ),
        ExpenseModel(
            id = 4,
            name = "ORANGE JUICE",
            quantity = 3,
            unitaryValue = BigDecimal("8.99"),
            isActive = false,
            type = ExpensesType.BUY
        )
    )
    val summary = SummaryModel(activeCount = 3, total = BigDecimal("150.72"))

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = TabPositions.BUYS,
                hasDeleteAllAction = true,
                onDeleteAllClick = {}
            )
        },
        bottomBar = { MainBottomBar(currentTab = TabPositions.BUYS, onTabSelected = {}) },
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
                itemCount = mockBuys.size,
                searchQuery = "",
                onSearch = {},
                onToggleAll = {}
            )
            ExpensesListScreen(
                modifier = Modifier.weight(1f),
                isLoading = false,
                flipper = null,
                items = mockBuys,
                onRefresh = {},
                onEdit = {},
                onActive = {},
                onDelete = {},
                showDate = false
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun BuysScreenPreview() {
    AccountantTheme { BuysPreviewContent() }
}