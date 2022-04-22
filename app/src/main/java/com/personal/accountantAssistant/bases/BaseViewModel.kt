package com.personal.accountantAssistant.bases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(
    private val analytics: AnalyticsProvider?
) : ViewModel(), CoroutineScope {

    private val job = Job()

    private var _isLoading = MutableLiveData(false)
    var isLoading: LiveData<Boolean> = _isLoading

    private var _flipper = MutableLiveData<FlipperViews>()
    var flipper: LiveData<FlipperViews> = _flipper

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

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
        analytics?.trackErrorEvent(message)
    }

}