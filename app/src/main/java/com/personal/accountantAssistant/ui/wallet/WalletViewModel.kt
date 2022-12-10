package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.useCases.SetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.wallet.*
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal

class WalletViewModel(
    private val getCardsUseCase: GetCardsUseCase,
    private val getCardsSummaryUseCase: GetCardsSummaryUseCase,
    private val setAvailableMoneyUseCase: SetAvailableMoneyUseCase,
    private val saveCardUseCase: SaveCardUseCase,
    private val activeCardsUseCase: ActiveCardsUseCase,
    private val deleteCardsUseCase: DeleteCardsUseCase,
    analytics: AnalyticsProvider? = null,
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _cards = MutableLiveData<MutableList<CardModel>?>()
    var cards: LiveData<MutableList<CardModel>?> = _cards

    fun loadCards() {
        launch {
            getCardsUseCase()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

    fun loadSummary() {
        launch {
            getCardsSummaryUseCase()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _summary.postValue(it) }
        }
    }

    fun setAvailableMoney(availableMoney: BigDecimal) {
        launch {
            setAvailableMoneyUseCase(availableMoney.toFloat())
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect()
        }
    }

    fun saveCard(model: CardModel) {
        launch {
            saveCardUseCase(model)
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

    fun setAllCardsActive(isActive: Boolean) {
        launch {
            activeCardsUseCase.setAllCardsActive(isActive)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

    fun switchActiveCard(model: CardModel) {
        launch {
            activeCardsUseCase.switchActiveCard(model)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

    fun deleteCard(model: CardModel) {
        launch {
            deleteCardsUseCase.deleteCard(model)
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

    fun deleteAllCards() {
        launch {
            deleteCardsUseCase.deleteAllCards()
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _cards.postValue(it) }
        }
    }

}