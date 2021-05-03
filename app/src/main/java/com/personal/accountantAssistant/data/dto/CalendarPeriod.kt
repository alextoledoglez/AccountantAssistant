package com.personal.accountantAssistant.data.dto

import com.personal.accountantAssistant.utils.DateUtils
import java.time.Period
import java.util.*

data class CalendarPeriod(private var start: Calendar?, private var end: Calendar?) {
    private var period: Period? = null

    init {
        period = DateUtils.getPeriodBetween(start, end)
    }

    companion object {
        fun initDefaultPeriodDates(): CalendarPeriod {
            val defaultPeriodDates = DateUtils.defaultPeriodDates
            val firstDate = defaultPeriodDates[0]
            val currentMonth = Calendar.getInstance()
            currentMonth.time = firstDate
            currentMonth.add(Calendar.DAY_OF_MONTH, -1)
            val lastDate = defaultPeriodDates[1]
            val nextMonth = Calendar.getInstance()
            nextMonth.time = lastDate
            nextMonth.add(Calendar.DAY_OF_MONTH, +1)
            return CalendarPeriod(currentMonth, nextMonth)
        }
    }

    fun getStart(): Calendar? {
        return start
    }

    fun setStart(start: Calendar?) {
        this.start = start
    }

    fun getEnd(): Calendar? {
        return end
    }

    fun setEnd(end: Calendar?) {
        this.end = end
    }

    fun getPeriod(): Period? {
        return period
    }

    fun setPeriod(period: Period?) {
        this.period = period
    }
}
