package com.personal.accountantAssistant.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WalletDetailsViewModel(private val repository: CardsRepository?) : BaseViewModel() {

    private var _isSaved = MutableLiveData(false)
    var isSaved: LiveData<Boolean> = _isSaved

    fun saveCard(model: CardModel) = launch {
        repository?.saveCard(model)?.collect { _isSaved.postValue(true) }
    }

}