package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository

class ActiveCardsUseCaseImpl(val repository: CardsRepository) : ActiveCardsUseCase {
    override fun switchActiveCard(model: CardModel) = repository.switchActiveCard(model)
    override fun setAllCardsActive(isActive: Boolean) = repository.setAllCardsActive(isActive)
}