package com.personal.accountantAssistant.data

import android.content.Context
import android.content.SharedPreferences
import com.personal.accountantAssistant.extensions.*
import java.math.BigDecimal
import java.util.*

class LocalStorage(val context: Context) {

    private fun getDefaultSharedPreferences(): SharedPreferences = context.getSharedPreferences(
        "${context.packageName}_preferences",
        Context.MODE_PRIVATE
    )

    fun setAvailableMoney(availableMoneyValue: Float) {
        getDefaultSharedPreferences().edit().putFloat(AVAILABLE_MONEY, availableMoneyValue).apply()
    }

    fun getAvailableMoney(): BigDecimal = getDefaultSharedPreferences().getFloat(
        AVAILABLE_MONEY, BigDecimal.ZERO.toFloat()
    ).toBigDecimal().rounded()

    private fun setFirstStrDate(firstDate: Date?) {
        getDefaultSharedPreferences().edit()
            .putString(FIRST_STR_DATE, firstDate.toDateStr()).apply()
    }

    fun getFirstDate() = getDefaultSharedPreferences().getString(
        FIRST_STR_DATE, String.EMPTY
    )?.toDate().orCurrent()

    private fun setLastStrDate(lastDate: Date?) {
        getDefaultSharedPreferences().edit().putString(LAST_STR_DATE, lastDate.toDateStr()).apply()
    }

    fun getLastDate(): Date = getDefaultSharedPreferences().getString(
        LAST_STR_DATE, String.EMPTY
    )?.toDate() ?: run {
        val nextMonth = Calendar.getInstance()
        nextMonth.add(Calendar.MONTH, 1)
        nextMonth.time
    }

    fun setPeriodDates(firstDate: Date?, lastDate: Date?) {
        setFirstStrDate(firstDate)
        setLastStrDate(lastDate)
    }

    fun setSignedAccountName(name: String?) {
        getDefaultSharedPreferences().edit().putString(SIGNED_ACCOUNT_NAME, name.orEmpty()).apply()
    }

    fun getSignedAccountName(): String? {
        return getDefaultSharedPreferences().getString(SIGNED_ACCOUNT_NAME, String.EMPTY)
    }

    companion object {
        private const val AVAILABLE_MONEY = "AVAILABLE_MONEY"
        private const val FIRST_STR_DATE = "FIRST_STR_DATE"
        private const val LAST_STR_DATE = "LAST_STR_DATE"
        private const val SIGNED_ACCOUNT_NAME = "SIGNED_ACCOUNT_NAME"
    }
}