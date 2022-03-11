package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.ListNotifyTypes
import com.personal.accountantAssistant.data.mappers.toWalletModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.orFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: CardsRepository?) : BaseViewModel() {

    private var _wallet = MutableLiveData<WalletModel>()
    var wallet: LiveData<WalletModel> = _wallet

    private fun getCards() = _wallet.value?.cards

    fun loadCards() = launch {
        repository?.getWallet()?.onStart { setLoading() }?.collect {
            _wallet.postValue(it)
            setData()
        }
    }

    fun restoreDefaultCards() = launch {
        repository?.setDefaultCards()?.collect { setListNotifier(ListNotifyTypes.INSERT_ALL) }
    }

    fun setAllCardsActive(isActive: Boolean) = launch {
        repository?.setAllCardsActive(isActive)?.collect {
            val cards = getCards()
            cards?.forEach { it.isActive = isActive }
            _wallet.postValue(cards?.toWalletModel())
            setListNotifier(ListNotifyTypes.ACTIVE_ALL)
        }
    }

    fun updateCard(position: Int, model: CardModel) = launch {
        repository?.updateCard(model)?.collect {
            val cards = getCards()
            cards?.filter { it.id == model.id }?.map { it.updateWith(model) }
            _wallet.postValue(cards?.toWalletModel())
            setListNotifier(ListNotifyTypes.UPDATE, position)
        }
    }

    fun deleteCard(position: Int, model: CardModel) = launch {
        repository?.deleteCard(model)?.collect {
            val cards = getCards()
            if (cards?.remove(model).orFalse()) {
                _wallet.postValue(cards?.toWalletModel())
                setListNotifier(ListNotifyTypes.DELETE, position)
            }
        }
    }

    fun deleteAllCards() = launch {
        repository?.deleteAllCards()?.collect {
            _wallet.value?.cards?.clear()
            setListNotifier(ListNotifyTypes.DELETE_ALL)
        }
    }

}