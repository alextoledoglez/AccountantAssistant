package com.personal.accountantAssistant.extensions

import android.view.Menu

private const val import_export_option = 0
private const val delete_all_option = 1
private const val restore_default_option = 2

private fun Menu.setMenuItemVisible(index: Int, isVisible: Boolean) {
    getItem(index)?.isVisible = isVisible
}

fun Menu.showMenuOptions(isVisible: Boolean = true) {
    setMenuItemVisible(import_export_option, isVisible)
    setMenuItemVisible(delete_all_option, isVisible)
    setMenuItemVisible(restore_default_option, isVisible)
}

fun Menu.hideMenuOptions() {
    showMenuOptions(isVisible = false)
}