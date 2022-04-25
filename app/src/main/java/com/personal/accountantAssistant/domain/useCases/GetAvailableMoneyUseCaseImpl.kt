package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.rounded
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

class GetAvailableMoneyUseCaseImpl(private val storage: LocalStorage?) : GetAvailableMoneyUseCase {

    override fun invoke(): Flow<BigDecimal?> = flowEmit {
        storage?.getFloat(LocalStorage.AVAILABLE_MONEY, BigDecimal.ZERO.toFloat())
            ?.toBigDecimal()
            ?.rounded()
    }
}