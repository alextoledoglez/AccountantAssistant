package com.personal.accountantAssistant.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.mappers.toUserModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.SignInService

class MenuViewModel(
    analytics: AnalyticsProvider?,
    private val localStorage: LocalStorage?
) : BaseViewModel(analytics) {

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    fun getUser() {
        setLoading()
        _user.postValue(SignInService.account?.toUserModel())
        setData()
    }

}