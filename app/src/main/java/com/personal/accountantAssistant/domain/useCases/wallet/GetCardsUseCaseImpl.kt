package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.repository.CardsRepository

class GetCardsUseCaseImpl(val repository: CardsRepository) : GetCardsUseCase {
    override fun invoke() = repository.getCards()
}