package com.personal.accountantAssistant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.theme.Dimens
import java.math.BigDecimal
import java.util.Calendar

@Composable
internal fun HomePreviewContent() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    val startDate = Calendar.getInstance().apply { set(2024, 9, 1) }.time
    val endDate = Calendar.getInstance().apply { set(2024, 9, 31) }.time

    val dashboardItems = listOf(
        DashboardItemModel(R.drawable.ic_buys, "Buys", primaryColor.toArgb(), BigDecimal("0.00")),
        DashboardItemModel(R.drawable.ic_bills, "Bills", errorColor.toArgb(), BigDecimal("3428.50")),
        DashboardItemModel(R.drawable.ic_money, "Missing", errorColor.toArgb(), BigDecimal("3428.50")),
        DashboardItemModel(R.drawable.ic_total, "Total", errorColor.toArgb(), BigDecimal("3428.50"))
    )

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = TabPositions.HOME,
                hasDeleteAllAction = false,
                onDeleteAllClick = {}
            )
        },
        bottomBar = { MainBottomBar(currentTab = TabPositions.HOME, onTabSelected = {}) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeHeader(
                periodDates = Pair(startDate, endDate),
                availableText = "AVAILABLE: \$ 1,500.00",
                availableColor = primaryColor,
                walletIconColor = primaryColor,
                onDatePickerClick = {}
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    horizontal = Dimens.spacingMd,
                    vertical = Dimens.spacingMd
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(dashboardItems) { item ->
                    HomeGridItem(item = item, onClick = {})
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AccountantTheme { HomePreviewContent() }
}