package com.personal.accountantAssistant.data.dto

import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.DateUtils
import java.util.*

data class CalendarValues(private var calendar: Calendar) {

    private var date: Date
    private var strDate: String? = null

    private var year = 0
    private var month = 0
    private var dayOfMonth = 0

    init {
        date = calendar.time
        strDate = DateUtils.toString(date)
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH] + 1
        dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
    }

    fun getCalendar(): Calendar {
        return calendar
    }

    fun setCalendar(calendar: Calendar) {
        this.calendar = calendar
    }

    fun getDate(): Date? {
        return date
    }

    fun setDate(date: Date) {
        this.date = date
    }

    fun getStrDate(): String? {
        return strDate
    }

    fun setStrDate(strDate: String?) {
        this.strDate = strDate
    }

    fun getYear(): Int {
        return year
    }

    fun setYear(year: Int) {
        this.year = year
    }

    fun getMonth(): Int {
        return month
    }

    fun setMonth(month: Int) {
        this.month = month
    }

    fun getDayOfMonth(): Int {
        return dayOfMonth
    }

    fun setDayOfMonth(dayOfMonth: Int) {
        this.dayOfMonth = dayOfMonth
    }

    fun toCurrentDateStr(): String {
        return "${getDayOfMonth()}${Constants.DASH_SEPARATOR}${getMonth()}${Constants.DASH_SEPARATOR}${getYear()}"
    }
}