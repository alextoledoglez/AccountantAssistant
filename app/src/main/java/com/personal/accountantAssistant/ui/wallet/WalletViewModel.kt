package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: CardsRepository?) : BaseViewModel() {

    private var _wallet = MutableLiveData<WalletModel>()
    var wallet: LiveData<WalletModel> = _wallet

    fun loadCards() = launch {
        repository?.getWallet()?.onStart { setLoading() }?.collect {
            _wallet.postValue(it)
            setData()
        }
    }

    fun restoreDefaultCards() = launch { repository?.setDefaultCards()?.collect() }

    fun setAllCardsActive(isActive: Boolean) = launch {
        repository?.setAllCardsActive(isActive)?.onStart { setLoading() }?.collect {
            setAllCheckedData(isActive)
        }
    }

    fun updateCard(model: CardModel) = launch {
        repository?.updateCard(model)?.onStart { setLoading() }?.collect { setUpdatedData(true) }
    }

    fun deleteCard(model: CardModel) = launch {
        repository?.deleteCard(model)?.onStart { setLoading() }?.collect { setDeletedData(true) }
    }

    fun deleteAllCards() = launch { repository?.deleteAllCards()?.collect() }

}