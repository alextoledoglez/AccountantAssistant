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
    private var isBuysViewSelected: Boolean = java.lang.Boolean.FALSE
    private var isBillsViewSelected: Boolean = java.lang.Boolean.FALSE

    @JvmStatic
    fun initializeHomeOptions() {
        isBuysViewSelected = java.lang.Boolean.FALSE
        isBillsViewSelected = java.lang.Boolean.FALSE
        enableMenuItemOptions(java.lang.Boolean.FALSE)
    }

    fun initializeSummaryOptions() {
        isBuysViewSelected = java.lang.Boolean.FALSE
        isBillsViewSelected = java.lang.Boolean.FALSE
        enableMenuItemOptions(java.lang.Boolean.FALSE)
    }

    @JvmStatic
    fun initializeBuysOptions() {
        isBuysViewSelected = java.lang.Boolean.TRUE
        isBillsViewSelected = java.lang.Boolean.FALSE
        enableMenuItemOptions(java.lang.Boolean.TRUE)
    }

    @JvmStatic
    fun initializeBillsOptions() {
        isBuysViewSelected = java.lang.Boolean.FALSE
        isBillsViewSelected = java.lang.Boolean.TRUE
        enableMenuItemOptions(java.lang.Boolean.TRUE)
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

    fun conditionalMenuItemClickListener(buysUnit: Unit?,
                                         billsUnit: Unit?) {
        conditionalMenuItemClickListener(ParserUtils.toAction(buysUnit), ParserUtils.toAction(billsUnit))
    }

    fun conditionalMenuItemClickListener(buysAction: Action?,
                                         billsAction: Action?) {
        if (isBuysViewSelected) {
            buysAction?.let { runAction(it) }
        } else if (isBillsViewSelected) {
            billsAction?.let { runAction(it) }
        }
    }
}