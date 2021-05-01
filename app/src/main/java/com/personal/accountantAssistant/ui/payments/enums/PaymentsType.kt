package com.personal.accountantAssistant.ui.payments.enums

import com.personal.accountantAssistant.utils.ParserUtils.isNullObject

enum class PaymentsType {
    BUY, BILL;

    companion object {
        fun isBuy(name: String): Boolean {
            return !isNullObject(name) && BUY.name == name
        }

        @JvmStatic
        fun isBuy(paymentsType: PaymentsType): Boolean {
            return !isNullObject(paymentsType) &&
                    isBuy(paymentsType.name)
        }

        fun isBill(name: String): Boolean {
            return !isNullObject(name) && BILL.name == name
        }

        @JvmStatic
        fun isBill(paymentsType: PaymentsType): Boolean {
            return !isNullObject(paymentsType) &&
                    isBill(paymentsType.name)
        }
    }
}