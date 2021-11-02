package com.personal.accountantAssistant.ui.payments.enums

import com.personal.accountantAssistant.utils.ParserUtils.isNullObject

enum class PaymentsType {
    BUY, BILL, NONE;

    companion object {
        fun isBuy(name: String?) = (!isNullObject(name) && BUY.name == name)

        fun isBuy(paymentsType: PaymentsType?) =
            (!isNullObject(paymentsType) && isBuy(paymentsType?.name))

        fun isBill(name: String?) = (!isNullObject(name) && BILL.name == name)

        fun isBill(paymentsType: PaymentsType?) =
            (!isNullObject(paymentsType) && isBill(paymentsType?.name))
    }
}