package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch

class WalletViewModel(
    analytics: AnalyticsProvider?, private val repository: CardsRepository?
) : BaseViewModel(analytics) {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _cards = MutableLiveData<MutableList<CardModel>?>()
    var cards: LiveData<MutableList<CardModel>?> = _cards

    fun loadCards() = launch {
        repository?.getCards()?.onStart { setLoading() }?.onError { setMessage(it.message) }
            ?.collect {
                _cards.postValue(it)
                setData()
            }
    }

    fun loadSummary() = launch {
        repository?.getSummary()
            ?.onError { setMessage(it.message) }
            ?.singleOrNull()
            ?.let { _summary.postValue(it) }
    }

    fun saveCard(model: CardModel) = launch {
        repository?.saveCard(model)?.onStart { setLoading() }?.onError { setMessage(it.message) }
            ?.collect {
                _cards.postValue(it)
                setData()
            }
    }

    fun restoreDefaultCards() = launch {
        repository?.setDefaultCards()?.onError { setMessage(it.message) }
            ?.collect { _cards.postValue(it) }
    }

    fun setAllCardsActive(isActive: Boolean) = launch {
        repository?.setAllCardsActive(isActive)
            ?.onError { setMessage(it.message) }
            ?.collect { _cards.postValue(it) }
    }

    fun switchActiveCard(model: CardModel) = launch {
        repository?.switchActiveCard(model)
            ?.onError { setMessage(it.message) }
            ?.collect { _cards.postValue(it) }
    }

    fun deleteCard(model: CardModel) = launch {
        repository?.deleteCard(model)?.onError { setMessage(it.message) }
            ?.collect { _cards.postValue(it) }
    }

    fun deleteAllCards() = launch {
        repository?.deleteAllCards()?.onError { setMessage(it.message) }
            ?.collect { _cards.postValue(it) }
    }

}