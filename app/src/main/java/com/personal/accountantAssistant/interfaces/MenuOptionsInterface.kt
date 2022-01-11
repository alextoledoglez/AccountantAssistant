package com.personal.accountantAssistant.interfaces

interface MenuOptionsInterface {
    abstract fun addMenuItemClickListener()
    abstract fun importMenuItemClickListener()
    abstract fun exportMenuItemClickListener()
    abstract fun deleteAllRecords()
    abstract fun restoreDefaultRecords()
}