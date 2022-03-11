package com.personal.accountantAssistant.data.enums

enum class ExpensesType {
    BUY, BILL, NONE;

    companion object {
        fun isBuy(name: String?) = (!name.isNullOrEmpty() && BUY.name == name)

        fun isBuy(type: ExpensesType?) = (type != null && isBuy(type.name))

        fun isBill(name: String?) = (!name.isNullOrEmpty() && BILL.name == name)

        fun isBill(type: ExpensesType?) = (type != null && isBill(type.name))
    }
}