package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.domain.models.buys.BuyModel

fun BuyModel.toExpense() = ExpenseEntity(this)