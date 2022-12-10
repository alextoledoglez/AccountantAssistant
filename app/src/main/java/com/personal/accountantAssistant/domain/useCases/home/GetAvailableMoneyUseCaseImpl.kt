package com.personal.accountantAssistant.domain.useCases.home

import com.personal.accountantAssistant.data.LocalStorage

class GetAvailableMoneyUseCaseImpl(private val storage: LocalStorage) : GetAvailableMoneyUseCase {

    override fun invoke() = storage.getAvailableMoney()
}