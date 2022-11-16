package com.personal.accountantAssistant.bases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(val analytics: AnalyticsProvider?) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private var _isLoading = MutableLiveData(false)
    var isLoading: LiveData<Boolean> = _isLoading

    private var _errorMessage = MutableLiveData<String?>()
    var errorMessage: LiveData<String?> = _errorMessage

    private var _flipper = MutableLiveData<FlipperViews>()
    var flipper: LiveData<FlipperViews> = _flipper

    fun setLoading() {
        _flipper.postValue(FlipperViews.LOADER)
        _isLoading.postValue(true)
    }

    fun setData() {
        _flipper.postValue(FlipperViews.DATA)
        _isLoading.postValue(false)
    }

    fun setMessage(message: String? = null) {
        _flipper.postValue(FlipperViews.MESSAGE)
        _isLoading.postValue(false)
        _errorMessage.postValue(message.orEmpty())
        analytics?.trackErrorEvent(message)
    }

}