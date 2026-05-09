package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import kotlinx.coroutines.flow.Flow

class SetAvailableMoneyUseCaseImpl(private val storage: LocalStorage) : SetAvailableMoneyUseCase {

    override fun invoke(value: Float): Flow<Unit> = storage.setAvailableMoney(value)
}