package com.personal.accountantAssistant.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.enums.NotificationTopics
import com.personal.accountantAssistant.data.mappers.toUserModel
import com.personal.accountantAssistant.domain.useCases.*
import com.personal.accountantAssistant.extensions.onError
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LoginViewModel(
    analytics: AnalyticsProvider?,
    private val getSignedUser: GetSignedUserUseCase,
    private val setSignedUser: SetSignedUserUseCase,
    private val getNotificationToken: GetNotificationTokenUseCase,
    private val setLocalNotificationToken: SetLocalNotificationTokenUseCase,
    private val subscribeNotificationTopic: SubscribeNotificationTopicUseCase
) : BaseViewModel(analytics) {

    private val _notificationToken = MutableLiveData<String?>()
    val notificationToken: LiveData<String?> get() = _notificationToken

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    private val _isNotificationTokenLoaded = MutableLiveData(false)
    val isNotificationTokenLoaded: LiveData<Boolean> get() = _isNotificationTokenLoaded

    private val _isLogged = MutableLiveData(false)
    val isLogged: LiveData<Boolean> get() = _isLogged

    fun getUser() {
        launch {
            getSignedUser()
                .onStart { _isProcessing.postValue(true) }
                .onError { setMessage(it.message) }
                .onCompletion { _isProcessing.postValue(false) }
                .collect { _userEmail.postValue(it?.email) }
        }
    }

    fun saveAccount(account: GoogleSignInAccount?) {
        launch {
            account?.toUserModel().apply {
                analytics?.setUserAccount(this)
                setSignedUser(this)
                    .onStart { _isProcessing.postValue(true) }
                    .onError { setMessage(it.message) }
                    .onCompletion { _isProcessing.postValue(false) }
                    .collect { _isLogged.postValue(it) }
            }
        }
    }

    fun setNotification() {
        launch {
            combine(getNotificationToken(), subscribeNotificationTopic()) { token, _ -> token }
                .onStart { _isProcessing.postValue(true) }
                .onError { setMessage(it.message) }
                .onCompletion { _isProcessing.postValue(false) }
                .collect { _notificationToken.postValue(it) }
        }
    }

    fun saveNotificationToken(token: String?) {
        launch {
            setLocalNotificationToken(token)
                .onStart { _isProcessing.postValue(true) }
                .onError { setMessage(it.message) }
                .onCompletion { _isProcessing.postValue(false) }
                .collect { _isNotificationTokenLoaded.postValue(it) }
        }
    }

}