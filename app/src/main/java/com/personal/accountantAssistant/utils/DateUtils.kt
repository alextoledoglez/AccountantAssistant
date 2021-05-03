package com.personal.accountantAssistant.utils

import com.personal.accountantAssistant.data.dto.CalendarValues
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

object DateUtils {
    private const val DD_MM_YYYY = "dd/MM/yyyy"

    @JvmStatic
    fun toDate(strDate: String): Date? {
        var date: Date? = Date()
        val dateFormat = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
        try {
            date = dateFormat.parse(strDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    @JvmStatic
    fun toString(date: Date?): String? {
        var strDate: String? = date.toString()
        val dateFormat = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
        try {
            strDate = date?.let { dateFormat.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strDate
    }

    @JvmStatic
    fun toCurrentDateStr(): String {
        val calendar = Calendar.getInstance()
        val calendarValues = CalendarValues(calendar)
        return calendarValues.toCurrentDateStr()
    }

    fun toPeriodStr(firstDate: Date?, lastDate: Date?): String {
        return toString(firstDate) + Constants.DASH_SEPARATOR + toString(lastDate)
    }

    private fun toCalendar(date: Date?): Calendar {
        val calendarDate = Calendar.getInstance()
        date?.let { calendarDate.time = it }
        return calendarDate
    }

    fun toCalendarMillis(date: Date?): Long {
        return toCalendar(date).timeInMillis
    }

    private fun toLocalDate(calendar: Calendar?): LocalDate? {
        return calendar?.let {
            val calendarValues = CalendarValues(it)
            LocalDate.of(calendarValues.getYear(), calendarValues.getMonth(), calendarValues.getDayOfMonth())
        }
    }

    private fun toLocalDate(date: Date?): LocalDate? {
        return date?.let { toCalendar(it) }?.let { toLocalDate(it) }
    }

    fun getPeriodBetween(start: Calendar?, end: Calendar?): Period {
        val startLocalDate = toLocalDate(start)
        val endLocalDate = toLocalDate(end)
        return Period.between(startLocalDate, endLocalDate)
    }

    private val defaultPeriodCalendars: List<Calendar>
        get() {
            val calendars: MutableList<Calendar> = ArrayList()
            val currentMonth = Calendar.getInstance()
            calendars.add(currentMonth)
            val nextMonth = Calendar.getInstance()
            nextMonth.add(Calendar.MONTH, 1)
            calendars.add(nextMonth)
            return calendars
        }

    private fun isNullPeriod(startDate: Calendar?, endDate: Calendar?): Boolean {
        return Objects.isNull(startDate) && Objects.isNull(endDate)
    }

    fun getDaysBetween(initDate: Date?, endDate: Date?): Int {
        return getDaysBetween(toCalendar(initDate), toCalendar(endDate))
    }

    private fun getDaysBetween(initDate: Calendar, endDate: Calendar): Int {
        if (isNullPeriod(initDate, endDate)) {
            return 0
        }
        val lInit = initDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val lEnd = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return Math.toIntExact(ChronoUnit.DAYS.between(lInit, lEnd))
    }

    @JvmStatic
    fun isInRange(compareDate: Date?, lastRangeDate: Date?): Boolean {
        val localCompareDate = toLocalDate(compareDate)
        val localLastRangeDate = toLocalDate(lastRangeDate)
        return localCompareDate?.isBefore(localLastRangeDate) == true ||
                localCompareDate?.isEqual(localLastRangeDate) == true
    }

    fun isInRange(compareDate: Date?, startRangeDate: Date?, lastRangeDate: Date?): Boolean {
        val localCompareDate = toLocalDate(compareDate)
        val localStartRangeDate = toLocalDate(startRangeDate)
        val localLastRangeDate = toLocalDate(lastRangeDate)
        return (localCompareDate?.isAfter(localStartRangeDate) == true ||
                localCompareDate?.isEqual(localStartRangeDate) == true) &&
                (localCompareDate.isBefore(localLastRangeDate) ||
                        localCompareDate.isEqual(localLastRangeDate))
    }

    val defaultPeriodDates: List<Date>
        get() = defaultPeriodCalendars
                .stream()
                .map { obj: Calendar -> obj.time }
                .collect(Collectors.toList())
}