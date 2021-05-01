package com.personal.accountantAssistant.db

import android.content.Context
import android.content.SharedPreferences
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import java.util.*

class LocalStorage(val context: Context) {

    companion object {
        private const val AVAILABLE_MONEY = "AVAILABLE_MONEY"
        private const val FIRST_STR_DATE = "FIRST_STR_DATE"
        private const val LAST_STR_DATE = "LAST_STR_DATE"
    }

    private fun getDefaultSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    }

    fun setAvailableMoney(availableMoneyValue: Float) {
        getDefaultSharedPreferences().edit().putFloat(AVAILABLE_MONEY, availableMoneyValue).apply()
    }

    fun getAvailableMoney(): Float {
        return getDefaultSharedPreferences().getFloat(AVAILABLE_MONEY, Constants.STR_DEFAULT_MONETARY_VALUE.toFloat())
    }

    private fun setFirstStrDate(firstDate: Date) {
        getDefaultSharedPreferences().edit().putString(FIRST_STR_DATE, DateUtils.toString(firstDate)).apply()
    }

    fun getFirstDate(): Date {
        val dateStr = getDefaultSharedPreferences().getString(FIRST_STR_DATE, Constants.EMPTY_STR)
        return dateStr?.let { DateUtils.toDate(it) } ?: run { Date() }
    }

    private fun setLastStrDate(lastDate: Date) {
        getDefaultSharedPreferences().edit().putString(LAST_STR_DATE, DateUtils.toString(lastDate)).apply()
    }

    fun getLastDate(): Date {
        val dateStr = getDefaultSharedPreferences().getString(LAST_STR_DATE, Constants.EMPTY_STR)
        return dateStr?.let { DateUtils.toDate(it) } ?: run {
            val nextMonth = Calendar.getInstance()
            nextMonth.add(Calendar.MONTH, 1)
            nextMonth.time
        }
    }

    fun setPeriodDates(firstDate: Date,
                       lastDate: Date) {
        setFirstStrDate(firstDate)
        setLastStrDate(lastDate)
    }

    fun getPeriodDates(): List<Date> {
        val dates: MutableList<Date> = ArrayList()
        dates.add(getFirstDate())
        dates.add(getLastDate())
        return dates
    }
}