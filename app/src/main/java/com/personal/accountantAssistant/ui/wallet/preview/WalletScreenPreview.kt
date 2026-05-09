package com.personal.accountantAssistant.ui.wallet.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.domain.enums.TabPositions
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.ui.common.ListSummaryCard
import com.personal.accountantAssistant.ui.common.MainBottomBar
import com.personal.accountantAssistant.ui.common.MainTopBar
import com.personal.accountantAssistant.ui.common.PrimaryFabButton
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.wallet.WalletListScreen
import java.math.BigDecimal

@PreviewScreenSizes
@PreviewLightDark
@Composable
fun WalletScreenPreview() {
    AccountantTheme { WalletPreviewContent() }
}

@Composable
internal fun WalletPreviewContent() {
    val mockCards = listOf(
        CardModel(
            id = 1,
            company = "ITAU",
            name = "LATAM PASS (CREDIT)",
            availableValue = BigDecimal("1500.00"),
            limitValue = BigDecimal("3000.00"),
            isActive = true
        ),
        CardModel(
            id = 2,
            company = "NUBANK",
            name = "CREDIT",
            availableValue = BigDecimal("2800.00"),
            limitValue = BigDecimal("5000.00"),
            isActive = true
        ),
        CardModel(
            id = 3,
            company = "ORIGINAL",
            name = "CREDIT",
            availableValue = BigDecimal("0.00"),
            isActive = false
        )
    )
    val summary = SummaryModel(activeCount = 2, total = BigDecimal("4300.00"))

    Scaffold(
        topBar = {
            MainTopBar(
                currentTab = TabPositions.WALLET,
                hasDeleteAllAction = true,
                onDeleteAllClick = {}
            )
        },
        bottomBar = { MainBottomBar(currentTab = TabPositions.WALLET, onTabSelected = {}) },
        floatingActionButton = {
            PrimaryFabButton(
                isVisible = true,
                painterResourceId = R.drawable.ic_add_white,
                stringResourceId = R.string.add_action,
                onClick = {}
            )
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
                itemCount = mockCards.size,
                searchQuery = "",
                onSearch = {},
                onToggleAll = {}
            )
            WalletListScreen(
                modifier = Modifier.weight(1f),
                isLoading = false,
                flipper = null,
                items = mockCards,
                onRefresh = {},
                onEdit = {},
                onActive = {},
                onDelete = {}
            )
        }
    }
}