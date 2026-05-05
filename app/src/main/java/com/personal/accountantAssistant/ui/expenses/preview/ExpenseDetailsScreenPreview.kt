package com.personal.accountantAssistant.ui.expenses.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.ui.expenses.ExpenseDetailsScreen
import com.personal.accountantAssistant.ui.theme.AccountantTheme
import java.math.BigDecimal
import java.util.Calendar

private fun dateOf(year: Int, month: Int, day: Int) =
    Calendar.getInstance().apply { set(year, month - 1, day) }.time

@Composable
internal fun ExpenseBuyDetailsPreviewContent() {
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

@Composable
internal fun ExpenseBillDetailsPreviewContent() {
    val mockExpense = ExpenseModel(
        id = 1,
        name = "ELECTRICITY",
        quantity = 1,
        date = dateOf(2024, 10, 13),
        unitaryValue = BigDecimal("1100.00"),
        isActive = true,
        type = ExpensesType.BILL
    )
    ExpenseDetailsScreen(
        expenseModel = mockExpense,
        onSave = {},
        onCancel = {}
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun ExpenseBuyDetailsScreenPreview() {
    AccountantTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { ExpenseBuyDetailsPreviewContent() }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_9_PRO)
@Composable
fun ExpenseBillDetailsScreenPreview() {
    AccountantTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { ExpenseBillDetailsPreviewContent() }
    }
}