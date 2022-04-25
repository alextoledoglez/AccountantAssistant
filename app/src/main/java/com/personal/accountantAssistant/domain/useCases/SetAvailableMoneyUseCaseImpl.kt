package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orZero
import kotlinx.coroutines.flow.Flow

class SetAvailableMoneyUseCaseImpl(private val storage: LocalStorage?) : SetAvailableMoneyUseCase {

    override fun invoke(value: Float): Flow<Unit> = flowEmit {
        storage?.edit()?.putFloat(LocalStorage.AVAILABLE_MONEY, value.orZero())?.apply()
    }
}