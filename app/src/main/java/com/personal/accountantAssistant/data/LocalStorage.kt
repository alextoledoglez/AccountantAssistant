package com.personal.accountantAssistant.data

import android.content.Context
import android.content.SharedPreferences
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import java.util.*

class LocalStorage(val context: Context) {

    companion object {
        private const val AVAILABLE_MONEY = "AVAILABLE_MONEY"
        private const val FIRST_STR_DATE = "FIRST_STR_DATE"
        private const val LAST_STR_DATE = "LAST_STR_DATE"
        private const val SIGNED_ACCOUNT_NAME = "SIGNED_ACCOUNT_NAME"
    }

    private fun getDefaultSharedPreferences(): SharedPreferences = context.getSharedPreferences(
            "${context.packageName}_preferences",
            Context.MODE_PRIVATE
    )

    fun setAvailableMoney(availableMoneyValue: Float) {
        getDefaultSharedPreferences().edit().putFloat(AVAILABLE_MONEY, availableMoneyValue).apply()
    }

    fun getAvailableMoney(): Float = getDefaultSharedPreferences().getFloat(
            AVAILABLE_MONEY,
            Constants.STR_DEFAULT_MONETARY_VALUE.toFloat()
    )

    fun getAvailableMoneyStr(): String = getAvailableMoney().toString()

    private fun setFirstStrDate(firstDate: Date?) {
        getDefaultSharedPreferences().edit()
                .putString(FIRST_STR_DATE, DateUtils.toString(firstDate)).apply()
    }

    fun getFirstDate(): Date = getDefaultSharedPreferences().getString(FIRST_STR_DATE, String.EMPTY)
            ?.let { DateUtils.toDate(it) } ?: run { Date() }

    private fun setLastStrDate(lastDate: Date?) {
        getDefaultSharedPreferences().edit().putString(LAST_STR_DATE, DateUtils.toString(lastDate))
                .apply()
    }

    fun getLastDate(): Date = getDefaultSharedPreferences().getString(LAST_STR_DATE, String.EMPTY)
            ?.let { DateUtils.toDate(it) } ?: run {
        val nextMonth = Calendar.getInstance()
        nextMonth.add(Calendar.MONTH, 1)
        nextMonth.time
    }

    fun setPeriodDates(firstDate: Date?, lastDate: Date?) {
        setFirstStrDate(firstDate)
        setLastStrDate(lastDate)
    }

    fun getPeriodDates(): List<Date> {
        val dates: MutableList<Date> = ArrayList()
        dates.add(getFirstDate())
        dates.add(getLastDate())
        return dates
    }

    fun setSignedAccountName(accountName: String?) {
        getDefaultSharedPreferences().edit().putString(SIGNED_ACCOUNT_NAME, accountName).apply()
    }

    fun getSignedAccountName(): String? {
        return getDefaultSharedPreferences().getString(SIGNED_ACCOUNT_NAME, null)
    }
}