package com.personal.accountantAssistant.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.personal.accountantAssistant.bases.BaseViewModel
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.MenuItemModel
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.providers.AnalyticsProvider
import kotlinx.coroutines.launch

class MenuViewModel(
    analytics: AnalyticsProvider?,
    private val localStorage: LocalStorage?
) : BaseViewModel(analytics) {

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _menus = MutableLiveData<List<MenuItemModel?>?>()
    val menus: LiveData<List<MenuItemModel?>?> get() = _menus

    fun loadUser() {
        launch {
            setLoading()
            localStorage?.getSignedUser()?.let { _user.postValue(it) }
            setData()
        }
    }

    fun loadMenus() {
        setLoading()
        val options = arrayListOf<MenuItemModel>()
        //options.add(MenuItemModel(0, R.drawable.ic_more_options, R.string.open_action))
        _menus.postValue(options)
        setData()
    }

}