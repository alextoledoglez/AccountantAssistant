package com.personal.accountantAssistant.data.enums.expenses

import com.personal.accountantAssistant.utils.ParserUtils.isNullObject

enum class ExpensesType {
    BUY, BILL, NONE;

    companion object {
        fun isBuy(name: String?) = (!isNullObject(name) && BUY.name == name)

        fun isBuy(expensesType: ExpensesType?) =
            (!isNullObject(expensesType) && isBuy(expensesType?.name))

        fun isBill(name: String?) = (!isNullObject(name) && BILL.name == name)

        fun isBill(expensesType: ExpensesType?) =
            (!isNullObject(expensesType) && isBill(expensesType?.name))
    }
}