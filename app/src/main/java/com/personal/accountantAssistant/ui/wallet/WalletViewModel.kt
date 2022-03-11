package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
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
        repository?.setAllCardsActive(isActive)?.collect {
            setListNotifier(ListNotifyTypes.UPDATE_ALL)
        }
    }

    fun updateCard(position: Int, model: CardModel) = launch {
        repository?.updateCard(model)?.collect { setListNotifier(ListNotifyTypes.UPDATE, position) }
    }

    fun deleteCard(position: Int, model: CardModel) = launch {
        repository?.deleteCard(model)?.collect { setListNotifier(ListNotifyTypes.DELETE, position) }
    }

    fun deleteAllCards() = launch {
        repository?.deleteAllCards()?.collect { setListNotifier(ListNotifyTypes.DELETE_ALL) }
    }

}