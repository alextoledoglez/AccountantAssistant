package com.personal.accountantAssistant.ui.payments

import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType

fun Payments.toBuys() = also { it.type = PaymentsType.BUY }

fun Payments.toBills() = also { it.type = PaymentsType.BILL }