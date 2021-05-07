package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.ui.bills.entities.Bills
import com.personal.accountantAssistant.ui.buys.entities.Buys
import com.personal.accountantAssistant.ui.payments.entities.Payments
import io.reactivex.functions.Action

object ParserUtils {
    @JvmStatic
    fun isNullObject(`object`: Any?): Boolean {
        return `object` == null
    }

    @JvmStatic
    fun isNotNullObject(`object`: Any?): Boolean {
        return !isNullObject(`object`)
    }

    fun isNotNullAndNotEmpty(list: List<*>?): Boolean {
        return list != null && list.isNotEmpty()
    }

    fun isNotNullAndNotEmpty(stringValue: String?): Boolean {
        return stringValue != null && stringValue.isNotEmpty()
    }

    private fun isNullOrEmpty(list: List<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    private fun isBuyInstance(entity: Any): Boolean {
        return entity is Buys
    }

    private fun isBillInstance(entity: Any): Boolean {
        return entity is Bills
    }

    fun toBills(entity: Any): Bills? {
        return if (isBillInstance(entity)) {
            entity as Bills
        } else {
            null
        }
    }

    fun toBuys(entity: Any): Buys? {
        return if (isBuyInstance(entity)) {
            entity as Buys
        } else {
            null
        }
    }

    @JvmStatic
    fun toPayments(entity: Any?): Payments? {
        var payments: Payments? = null
        entity?.let {
            when {
                isBuyInstance(it) -> {
                    payments = Payments(it as Buys)
                }
                isBillInstance(it) -> {
                    payments = Payments(it as Bills)
                }
                isPaymentInstance(it) -> {
                    payments = it as Payments
                }
            }
        }
        return payments
    }

    private fun isPaymentInstance(entity: Any): Boolean {
        return entity is Payments
    }
}