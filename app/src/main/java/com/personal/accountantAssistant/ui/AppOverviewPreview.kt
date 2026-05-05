package com.personal.accountantAssistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.ui.bills.preview.BillsPreviewContent
import com.personal.accountantAssistant.ui.buys.preview.BuysPreviewContent
import com.personal.accountantAssistant.ui.home.preview.HomePreviewContent
import com.personal.accountantAssistant.ui.menu.preview.MenuPreviewContent
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.wallet.preview.WalletPreviewContent

@Preview(widthDp = 2560, heightDp = 1440)
@Composable
fun AppOverviewPreview() {
    AccountantTheme {
        Row(
            modifier = Modifier.fillMaxSize().padding(Dimens.paddingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { HomePreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { WalletPreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { BuysPreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { BillsPreviewContent() }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) { MenuPreviewContent() }
        }
    }
}