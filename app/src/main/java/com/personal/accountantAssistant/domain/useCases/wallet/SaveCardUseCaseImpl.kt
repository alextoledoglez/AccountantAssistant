package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository

class SaveCardUseCaseImpl(val repository: CardsRepository) : SaveCardUseCase {
    override fun invoke(model: CardModel) = repository.saveCard(model)
}