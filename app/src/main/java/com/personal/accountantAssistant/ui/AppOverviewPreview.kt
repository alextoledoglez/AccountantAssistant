package com.personal.accountantAssistant.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.ui.bills.BillsPreviewContent
import com.personal.accountantAssistant.ui.buys.BuysPreviewContent
import com.personal.accountantAssistant.ui.home.HomePreviewContent
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.wallet.WalletPreviewContent

@Preview(widthDp = 1600, heightDp = 900)
@Composable
fun AppOverviewPreview() {
    AccountantTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { HomePreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { WalletPreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { BuysPreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { BillsPreviewContent() }
        }
    }
}