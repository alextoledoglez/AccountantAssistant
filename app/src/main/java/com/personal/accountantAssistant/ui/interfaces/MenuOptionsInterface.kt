package com.personal.accountantAssistant.ui.interfaces

interface MenuOptionsInterface {
    abstract fun addMenuItemClickListener()
    abstract fun importMenuItemClickListener()
    abstract fun exportMenuItemClickListener()
    abstract fun deleteAllRecords()
    abstract fun restoreDefaultRecords()
}