package com.personal.accountantAssistant.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.mappers.toUserModel
import com.personal.accountantAssistant.domain.repository.UserRepository
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LoginViewModel(
    analytics: AnalyticsProvider?, private val userRepository: UserRepository?
) : BaseViewModel(analytics) {

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    private val _isLogged = MutableLiveData(false)
    val isLogged: LiveData<Boolean> get() = _isLogged

    fun getUser() {
        launch {
            userRepository?.getSignedUser()
                ?.onStart { _isProcessing.postValue(true) }
                ?.onError { setMessage(it.message) }
                ?.onCompletion { _isProcessing.postValue(false) }
                ?.collect { _userEmail.postValue(it?.email) }
        }
    }

    fun saveAccount(account: GoogleSignInAccount?) {
        launch {
            account?.toUserModel().apply {
                analytics?.setUserAccount(this)
                userRepository?.setSignedUser(this)
                    ?.onStart { _isProcessing.postValue(true) }
                    ?.onError { setMessage(it.message) }
                    ?.onCompletion { _isProcessing.postValue(false) }
                    ?.collect { _isLogged.postValue(this?.email?.isNotBlank().orFalse()) }
            }
        }
    }

}