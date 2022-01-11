package com.personal.accountantAssistant.ui.payments

import com.personal.accountantAssistant.ui.payments.entities.PaymentsEntity
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType

fun PaymentsEntity.toBuys() = also { it.type = PaymentsType.BUY }

fun PaymentsEntity.toBills() = also { it.type = PaymentsType.BILL }