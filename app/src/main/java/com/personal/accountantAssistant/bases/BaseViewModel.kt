package com.personal.accountantAssistant.bases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.personal.accountantAssistant.data.enums.FlipperViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    private var _isAllChecked = MutableLiveData(false)
    var isAllChecked: LiveData<Boolean> = _isAllChecked

    private var _isUpdated = MutableLiveData(false)
    var isUpdated: LiveData<Boolean> = _isUpdated

    private var _isDeleted = MutableLiveData(false)
    var isDeleted: LiveData<Boolean> = _isDeleted

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

    fun setAllCheckedData(isChecked: Boolean) {
        _isAllChecked.postValue(isChecked)
        setData()
    }

    fun setUpdatedData(isUpdated: Boolean) {
        _isUpdated.postValue(isUpdated)
        setData()
    }

    fun setDeletedData(isDeleted: Boolean) {
        _isDeleted.postValue(isDeleted)
        setData()
    }

    fun setMessage() {
        _flipper.postValue(FlipperViews.MESSAGE)
        _isLoading.postValue(false)
    }
}