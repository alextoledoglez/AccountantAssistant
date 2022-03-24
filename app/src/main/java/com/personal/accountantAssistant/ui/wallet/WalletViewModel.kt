package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.replaceActiveStateOf
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.orFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: CardsRepository?) : BaseViewModel() {

    private var _summary = MutableLiveData<SummaryModel>()
    var summary: LiveData<SummaryModel> = _summary

    private var _cards = MutableLiveData<MutableList<CardModel>?>()
    var cards: LiveData<MutableList<CardModel>?> = _cards

    private fun getCards() = _cards.value

    fun loadCards() = launch {
        repository?.getCards()?.onStart { setLoading() }?.collect {
            _cards.postValue(it)
            setData()
        }
    }

    fun loadSummary(list: MutableList<CardModel>?) {
        _summary.postValue(list?.toSummaryModel())
    }

    fun restoreDefaultCards() = launch {
        repository?.setDefaultCards()?.collect { }
    }

    fun setAllCardsActive(isActive: Boolean) = launch {
        repository?.setAllCardsActive(isActive)?.collect { _cards.postValue(it) }
    }

    fun updateCard(model: CardModel) = launch {
        val list = getCards()?.replaceActiveStateOf(model)
        repository?.updateCard(model)?.collect { _cards.postValue(list) }
    }

    fun deleteCard(model: CardModel) = launch {
        val list = getCards()
        repository?.deleteCard(model)?.collect {
            if (list?.remove(model).orFalse())
                _cards.postValue(list)
        }
    }

    fun deleteAllCards() = launch {
        val list = getCards()
        repository?.deleteAllCards()?.collect {
            list?.clear()
            _cards.postValue(list)
        }
    }

}