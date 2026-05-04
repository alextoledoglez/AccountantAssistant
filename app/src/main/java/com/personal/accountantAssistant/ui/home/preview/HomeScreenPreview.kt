package com.personal.accountantAssistant.ui.home.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.home.components.HomeHeader
import com.personal.accountantAssistant.ui.home.components.HomeLandscapeContent
import com.personal.accountantAssistant.ui.home.components.HomePortraitContent
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal
import java.util.Calendar

@Composable
internal fun HomePreviewContent(isLandscape: Boolean = false) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    val startDate = Calendar.getInstance().apply { set(2024, 9, 1) }.time
    val endDate = Calendar.getInstance().apply { set(2024, 9, 31) }.time

    val dashboardItems = listOf(
        DashboardItemModel(R.drawable.ic_buys, "Buys", primaryColor.toArgb(), BigDecimal("0.00")),
        DashboardItemModel(
            R.drawable.ic_bills,
            "Bills",
            errorColor.toArgb(),
            BigDecimal("3428.50")
        ),
        DashboardItemModel(
            R.drawable.ic_money,
            "Missing",
            errorColor.toArgb(),
            BigDecimal("3428.50")
        ),
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
            if (isLandscape) {
                HomeLandscapeContent(
                    periodDates = Pair(startDate, endDate),
                    availableText = "AVAILABLE: \$ 1,500.00",
                    availableColor = primaryColor,
                    walletIconColor = primaryColor,
                    dashboardItems = dashboardItems,
                    onDatePickerClick = {},
                    navigateTo = {}
                )
            } else {
                HomeHeader(
                    periodDates = Pair(startDate, endDate),
                    availableText = "AVAILABLE: \$ 1,500.00",
                    availableColor = primaryColor,
                    walletIconColor = primaryColor,
                    onDatePickerClick = {}
                )

                HomePortraitContent(
                    dashboardItems = dashboardItems,
                    navigateTo = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun HomeScreenPreview() {
    AccountantTheme { HomePreviewContent() }
}

@Preview(showBackground = true, device = "spec:width=891dp,height=411dp,dpi=420")
@Composable
fun HomeScreenLandscapePreview() {
    AccountantTheme { HomePreviewContent(isLandscape = true) }
}
