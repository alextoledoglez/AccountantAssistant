package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: CardsRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _cards = MutableLiveData<MutableList<CardModel>?>()
    var cards: LiveData<MutableList<CardModel>?> = _cards

    fun loadCards() = launch {
        repository?.getCards()?.onStart { setLoading() }?.collect {
            _cards.postValue(it)
            setData()
        }
    }

    fun loadSummary(list: MutableList<CardModel>?) {
        _summary.postValue(list?.toSummaryModel())
    }

    fun saveCard(model: CardModel) = launch {
        repository?.saveCard(model)?.onStart { setLoading() }?.collect {
            _cards.postValue(it)
            setData()
        }
    }

    fun restoreDefaultCards() = launch {
        repository?.setDefaultCards()?.collect { _cards.postValue(it) }
    }

    fun setAllCardsActive(isActive: Boolean) = launch {
        repository?.setAllCardsActive(isActive)?.collect { _cards.postValue(it) }
    }

    fun switchActiveCard(model: CardModel) = launch {
        repository?.switchActiveCard(model)?.collect { _cards.postValue(it) }
    }

    fun deleteCard(model: CardModel) = launch {
        repository?.deleteCard(model)?.collect { _cards.postValue(it) }
    }

    fun deleteAllCards() = launch {
        repository?.deleteAllCards()?.collect { _cards.postValue(it) }
    }

}