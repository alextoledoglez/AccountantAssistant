package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.ui.bills.entities.Bills
import com.personal.accountantAssistant.ui.buys.entities.Buys
import com.personal.accountantAssistant.ui.payments.entities.Payments

object ParserUtils {
    @JvmStatic
    fun isNullObject(`object`: Any?): Boolean {
        return `object` == null
    }

    private fun isBuyInstance(entity: Any?): Boolean {
        return entity is Buys
    }

    private fun isBillInstance(entity: Any?): Boolean {
        return entity is Bills
    }

    fun toPayments(entity: Any?) = when {
        isBuyInstance(entity) -> Payments(entity as Buys)
        isBillInstance(entity) -> Payments(entity as Bills)
        isPaymentInstance(entity) -> entity as Payments
        else -> null
    }

    private fun isPaymentInstance(entity: Any?): Boolean {
        return entity is Payments
    }
}