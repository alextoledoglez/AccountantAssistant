package com.personal.accountantAssistant.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.domain.useCases.GetSignedUserUseCase
import com.personal.accountantAssistant.domain.useCases.SetSignedUserUseCase
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MenuViewModel(
    analytics: AnalyticsProvider?,
    private val getSignedUser: GetSignedUserUseCase,
    private val setSignedUser: SetSignedUserUseCase
) : BaseViewModel(analytics) {

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _isLoggedOut = MutableLiveData(false)
    val isLoggedOut: LiveData<Boolean> get() = _isLoggedOut

    fun loadUser() {
        launch {
            getSignedUser()
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _user.postValue(it) }
        }
    }

    fun clearUser() {
        launch {
            analytics?.setUserAccount(user = null)
            setSignedUser(user = null)
                .onStart { setLoading() }
                .onError { setMessage(it.message) }
                .onCompletion { setData() }
                .collect { _isLoggedOut.postValue(it) }
        }
    }

}