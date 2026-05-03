package com.personal.accountantAssistant.ui.wallet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal
import java.util.Calendar

@Composable
internal fun WalletDetailsPreviewContent() {
    val mockCard = CardModel(
        id = 1,
        company = "ITAU",
        name = "LATAM PASS (CREDIT)",
        availableValue = BigDecimal("1500.00"),
        limitValue = BigDecimal("3000.00"),
        password = "1234",
        date = Calendar.getInstance().apply { set(2024, 9, 10) }.time,
        isActive = true
    )
    WalletDetailsScreen(
        cardModel = mockCard,
        onSave = {},
        onCancel = {}
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun WalletDetailsScreenPreview() {
    AccountantTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { WalletDetailsPreviewContent() }
    }
}