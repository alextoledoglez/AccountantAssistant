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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.ui.common.AdaptiveScreen
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.home.components.HomeHeader
import com.personal.accountantAssistant.ui.home.components.HomeLandscapeContent
import com.personal.accountantAssistant.ui.home.components.HomePortraitContent
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal
import java.util.Calendar

@PreviewScreenSizes
@PreviewLightDark
@Composable
fun HomeScreenPreview() {
    AccountantTheme { HomePreviewContent() }
}

@Composable
internal fun HomePreviewContent() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    val startDate = Calendar.getInstance().apply { set(2024, 9, 1) }.time
    val endDate = Calendar.getInstance().apply { set(2024, 9, 31) }.time

    val dashboardItems = listOf(
        DashboardItemModel(
            drawableRes = R.drawable.ic_buys,
            textRes = R.string.menu_buys,
            color = primaryColor.toArgb(),
            value = BigDecimal("0.00")
        ),
        DashboardItemModel(
            drawableRes = R.drawable.ic_bills,
            textRes = R.string.menu_bills,
            color = errorColor.toArgb(),
            value = BigDecimal("3428.50")
        ),
        DashboardItemModel(
            drawableRes = R.drawable.ic_money,
            textRes = R.string.missing,
            color = errorColor.toArgb(),
            value = BigDecimal("3428.50")
        ),
        DashboardItemModel(
            drawableRes = R.drawable.ic_total,
            textRes = R.string.total,
            color = errorColor.toArgb(),
            value = BigDecimal("3428.50")
        )
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
            AdaptiveScreen(
                portrait = {
                    HomeHeader(
                        periodDates = Pair(startDate, endDate),
                        availableText = "AVAILABLE: \$ 1,500.00",
                        availableColor = primaryColor,
                        walletIconColor = primaryColor,
                        onDatePickerClick = {}
                    )
                    HomePortraitContent(dashboardItems = dashboardItems, navigateTo = {})
                },
                landscape = {
                    HomeLandscapeContent(
                        periodDates = Pair(startDate, endDate),
                        availableText = "AVAILABLE: \$ 1,500.00",
                        availableColor = primaryColor,
                        walletIconColor = primaryColor,
                        dashboardItems = dashboardItems,
                        onDatePickerClick = {},
                        navigateTo = {}
                    )
                }
            )
        }
    }
}
