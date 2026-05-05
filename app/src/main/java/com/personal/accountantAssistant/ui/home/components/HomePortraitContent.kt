package com.personal.accountantAssistant.ui.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.ui.theme.Dimens

@Composable
internal fun HomePortraitContent(
    dashboardItems: List<DashboardItemModel>,
    navigateTo: (tab: TabPositions) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        contentPadding = PaddingValues(horizontal = Dimens.spacingMd, vertical = Dimens.spacingMd),
        modifier = Modifier.fillMaxSize()
    ) {
        items(dashboardItems) { item ->
            HomeGridItem(item = item, onClick = { navigateTo(item.toTabPosition()) })
        }
    }
}