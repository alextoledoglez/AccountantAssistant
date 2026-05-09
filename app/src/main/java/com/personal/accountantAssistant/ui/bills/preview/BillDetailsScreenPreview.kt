package com.personal.accountantAssistant.ui.bills.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.dateOf
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsScreen
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal
import java.util.Calendar

@PreviewScreenSizes
@PreviewLightDark
@Composable
fun BillDetailsScreenPreview() {
    AccountantTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { BillDetailsPreviewContent() }
    }
}

@Composable
internal fun BillDetailsPreviewContent() {
    val mockExpense = ExpenseModel(
        id = 1,
        name = "ELECTRICITY",
        quantity = 1,
        date = Calendar.getInstance().dateOf(2024, 10, 13),
        unitaryValue = BigDecimal("1100.00"),
        isActive = true,
        type = ExpensesType.BILL
    )
    ExpenseDetailsScreen(expenseModel = mockExpense, onSave = {}, onCancel = {})
}