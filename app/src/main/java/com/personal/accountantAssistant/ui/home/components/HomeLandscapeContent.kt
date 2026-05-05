package com.personal.accountantAssistant.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.ui.theme.Dimens
import java.util.Date

@Composable
internal fun HomeLandscapeContent(
    periodDates: Pair<Date?, Date?>?,
    availableText: String,
    availableColor: Color,
    walletIconColor: Color,
    dashboardItems: List<DashboardItemModel>,
    onDatePickerClick: () -> Unit,
    navigateTo: (tab: TabPositions) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        HomeHeader(
            periodDates = periodDates,
            availableText = availableText,
            availableColor = availableColor,
            walletIconColor = walletIconColor,
            onDatePickerClick = onDatePickerClick,
            modifier = Modifier.weight(0.9f)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            contentPadding = PaddingValues(all = Dimens.spacingXs),
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
        ) {
            items(dashboardItems) { item ->
                HomeGridItem(item = item, onClick = { navigateTo(item.toTabPosition()) })
            }
        }
    }
}