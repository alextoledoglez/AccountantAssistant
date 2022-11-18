package com.personal.accountantAssistant.providers

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.ZERO
import com.personal.accountantAssistant.extensions.flowEmit
import java.math.BigDecimal
import java.util.*

object MockCardsProviders {

    fun mockedCard() = CardModel(
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

    fun mockedCardsFlow() = flowEmit {
        mutableListOf(
            mockedCard().copy(id = 1),
            mockedCard().copy(id = 2)
        )
    }

    fun mockedAllCardsActiveFlow() = flowEmit {
        mutableListOf(
            mockedCard().copy(id = 1, isActive = true),
            mockedCard().copy(id = 2, isActive = true)
        )
    }

    fun mockedAllCardsInactiveFlow() = flowEmit {
        mutableListOf(
            mockedCard().copy(id = 1, isActive = false),
            mockedCard().copy(id = 2, isActive = false)
        )
    }

    fun mockedCardSummaryFlow() = flowEmit {
        SummaryModel(activeCount = Int.ZERO, total = BigDecimal.ZERO)
    }

    fun mockedAvailableValue(): BigDecimal = BigDecimal.ZERO
}