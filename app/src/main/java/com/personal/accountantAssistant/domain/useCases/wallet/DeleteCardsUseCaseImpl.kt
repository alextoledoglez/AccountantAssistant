package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository

class DeleteCardsUseCaseImpl(val repository: CardsRepository) : DeleteCardsUseCase {
    override fun deleteCard(model: CardModel) = repository.deleteCard(model)
    override fun deleteAllCards() = repository.deleteAllCards()
}