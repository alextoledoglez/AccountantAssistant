package com.personal.accountantAssistant.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.EditText
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.dto.CalendarPeriod
import java.util.*

object DatePickerDialogUtils {
    @JvmStatic
    fun setDatePickerDialogFrom(
        context: Context?,
        dateEditText: EditText,
        defaultDateText: String?
    ) {
        dateEditText.setText(defaultDateText)
        val defaultCalendar = Calendar.getInstance()
        defaultDateText?.let { DateUtils.toDate(it) }?.let { defaultCalendar.time = it }
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val newDate = Calendar.getInstance()
                    newDate[year, monthOfYear] = dayOfMonth
                    dateEditText.setText(DateUtils.toString(newDate.time))
                },
                defaultCalendar[Calendar.YEAR],
                defaultCalendar[Calendar.MONTH],
                defaultCalendar[Calendar.DAY_OF_MONTH]
            )
        }
        dateEditText.setOnClickListener { datePickerDialog?.show() }
        dateEditText.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                datePickerDialog?.show()
            }
        }
    }

    fun initializeCalendarPickerView(
        calendarPickerView: CalendarView,
        selectedPeriodAction: () -> Unit,
        selectedDateAction: () -> Unit
    ) {
        val localStorage = LocalStorage(calendarPickerView.context)
        val calendarPeriod: CalendarPeriod = CalendarPeriod.initDefaultPeriodDates()
        val defaultTimeInMillis = Calendar.getInstance().timeInMillis
        val calendarStart = calendarPeriod.getStart()
        val calendarEnd = calendarPeriod.getEnd()

        calendarPickerView.apply {
            minDate = calendarStart?.timeInMillis ?: defaultTimeInMillis
            maxDate = calendarEnd?.timeInMillis ?: defaultTimeInMillis
        }
/*         calendarPickerView.init(calendarStart?.time, calendarEnd?.time)
                 .inMode(CalendarView.SelectionMode.RANGE)
                 .withSelectedDates(localStorage.getPeriodDates().stream().filter { periodDate: Date? ->
                     DateUtils.isInRange(periodDate, calendarStart?.time, calendarEnd?.time)
                 }?.collect(Collectors.toList<Date>()))*/
/*         val selectedDates: MutableList<Date> = ArrayList()
         calendarPickerView.setTypeface(Typeface.SANS_SERIF)
         calendarPickerView.setOnDateSelectedListener(object : OnDateSelectedListener {
             override fun onDateSelected(date: Date) {
                 selectedDates.add(date)
                 if (selectedDates.size == 2) {
                     val firstSelectedDate = selectedDates[0]
                     val lastSelectedDate = selectedDates[1]
                     localStorage.setPeriodDates(firstSelectedDate, lastSelectedDate)
                     runAction(selectedPeriodAction)
                 }
                 runAction(selectedDateAction)
             }

             override fun onDateUnselected(date: Date) {
                 selectedDates.clear()
             }
         })*/
    }
}