package com.personal.accountantAssistant.utils

import android.view.Menu
import com.personal.accountantAssistant.utils.ActionUtils.runAction
import io.reactivex.functions.Action

object MenuHelper {
    var mainMenu: Menu? = null
    private const val add_option = 0
    private const val import_export_option = 1
    private const val delete_all_option = 2
    private const val restore_default_option = 3
    private var isBuysViewSelected: Boolean = false
    private var isBillsViewSelected: Boolean = false

    @JvmStatic
    fun initializeHomeOptions() {
        isBuysViewSelected = false
        isBillsViewSelected = false
        enableMenuItemOptions(false)
    }

    fun initializeSummaryOptions() {
        isBuysViewSelected = false
        isBillsViewSelected = false
        enableMenuItemOptions(false)
    }

    @JvmStatic
    fun initializeBuysOptions() {
        isBuysViewSelected = true
        isBillsViewSelected = false
        enableMenuItemOptions(true)
    }

    @JvmStatic
    fun initializeBillsOptions() {
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

    fun conditionalMenuItemClickListener(buysAction: Action?, billsAction: Action?) {
        if (isBuysViewSelected)
            buysAction?.let { runAction(it) }
        else (isBillsViewSelected)
        billsAction?.let { runAction(it) }
    }

}