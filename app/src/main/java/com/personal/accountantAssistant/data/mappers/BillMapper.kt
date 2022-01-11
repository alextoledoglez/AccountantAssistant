package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.domain.models.bills.BillModel

fun BillModel.toExpense() = ExpenseEntity(this)