package com.personal.accountantAssistant.utils

import android.view.Menu

object MenuHelper {
    var mainMenu: Menu? = null
    private const val add_option = 0
    private const val import_export_option = 1
    private const val delete_all_option = 2
    private const val restore_default_option = 3
    private var isWalletViewSelected: Boolean = false
    private var isBuysViewSelected: Boolean = false
    private var isBillsViewSelected: Boolean = false

    @JvmStatic
    fun initializeHomeOptions() {
        isWalletViewSelected = false
        isBuysViewSelected = false
        isBillsViewSelected = false
        enableMenuItemOptions(false)
    }

    @JvmStatic
    fun initializeWalletOptions() {
        isWalletViewSelected = true
        isBuysViewSelected = false
        isBillsViewSelected = false
        enableMenuItemOptions(true)
    }

    @JvmStatic
    fun initializeBuysOptions() {
        isWalletViewSelected = false
        isBuysViewSelected = true
        isBillsViewSelected = false
        enableMenuItemOptions(true)
    }

    @JvmStatic
    fun initializeBillsOptions() {
        isWalletViewSelected = false
        isBuysViewSelected = false
        isBillsViewSelected = true
        enableMenuItemOptions(true)
    }

    fun enableMenuItemOptions(enable: Boolean) {
        setItemEnabled(add_option, enable)
        setItemEnabled(import_export_option, enable)
        setItemEnabled(delete_all_option, enable)
        setItemEnabled(restore_default_option, enable)
    }

    private fun setItemEnabled(itemIndex: Int, enabled: Boolean) {
        mainMenu?.getItem(itemIndex)?.isVisible = enabled
    }

}