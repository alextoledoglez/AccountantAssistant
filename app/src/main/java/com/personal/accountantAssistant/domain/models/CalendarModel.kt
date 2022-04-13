package com.personal.accountantAssistant.domain.models

import com.personal.accountantAssistant.extensions.DASH_SEPARATOR
import com.personal.accountantAssistant.extensions.toDateStr
import java.util.*

data class CalendarModel(private var calendar: Calendar = Calendar.getInstance()) {

    private var date: Date
    private var strDate: String? = null

    private var year = 0
    private var month = 0
    private var dayOfMonth = 0

    init {
        date = calendar.time
        strDate = date.toDateStr()
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
        return "${getDayOfMonth()}${String.DASH_SEPARATOR}${getMonth()}${String.DASH_SEPARATOR}${getYear()}"
    }
}