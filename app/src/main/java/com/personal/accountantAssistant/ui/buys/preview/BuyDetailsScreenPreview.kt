package com.personal.accountantAssistant.ui.buys.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsScreen
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal

@PreviewScreenSizes
@PreviewLightDark
@Composable
fun BuyDetailsScreenPreview() {
    AccountantTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { BuyDetailsPreviewContent() }
    }
}

@Composable
internal fun BuyDetailsPreviewContent() {
    val mockExpense = ExpenseModel(
        id = 1,
        name = "EGGS",
        quantity = 2,
        unitaryValue = BigDecimal("22.90"),
        isActive = true,
        type = ExpensesType.BUY
    )
    ExpenseDetailsScreen(
        expenseModel = mockExpense,
        onSave = {},
        onCancel = {}
    )
}