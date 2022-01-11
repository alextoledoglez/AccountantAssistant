package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.ui.bills.entities.Bills
import com.personal.accountantAssistant.ui.buys.entities.Buys
import com.personal.accountantAssistant.ui.payments.entities.PaymentsEntity
import com.personal.accountantAssistant.ui.wallet.entities.CardEntity

object ParserUtils {
    @JvmStatic
    fun isNullObject(`object`: Any?): Boolean {
        return `object` == null
    }

    private fun isCardInstance(entity: Any?): Boolean {
        return entity is CardEntity
    }

    private fun isBuyInstance(entity: Any?): Boolean {
        return entity is Buys
    }

    private fun isBillInstance(entity: Any?): Boolean {
        return entity is Bills
    }

    fun toPayments(entity: Any?) = when {
        isBuyInstance(entity) -> PaymentsEntity(entity as Buys)
        isBillInstance(entity) -> PaymentsEntity(entity as Bills)
        isPaymentInstance(entity) -> entity as PaymentsEntity
        else -> null
    }

    private fun isPaymentInstance(entity: Any?): Boolean {
        return entity is PaymentsEntity
    }
}