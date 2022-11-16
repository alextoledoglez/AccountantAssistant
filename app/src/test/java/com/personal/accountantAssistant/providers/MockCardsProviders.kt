package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockCardsProviders {

    fun mockedFlowCards() = flowEmit {
        mutableListOf(
            CardModel(
                id = Long.ZERO,
                company = "Bank abc",
                name = "Credit card",
                date = Date(),
                password = String.EMPTY,
                usedValue = BigDecimal(400),
                availableValue = BigDecimal(600),
                limitValue = BigDecimal(1000),
                isActive = false
            )
        )
    }
}