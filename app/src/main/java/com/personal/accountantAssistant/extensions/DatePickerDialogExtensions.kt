package com.personal.accountantAssistant.extensions

import android.app.DatePickerDialog
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import java.util.*

private const val SCROLL_ANIMATOR_NAME = "animator"
private const val CALENDAR_VIEW_NAME = "date_picker_day_picker"
private const val PREV_BUTTON_NAME = "prev"
private const val NEXT_BUTTON_NAME = "next"
private const val TYPE = "id"
private const val PACKAGE = "android"

private fun DatePickerDialog.getIdentifier(name: String, defType: String, defPackage: String) =
    context.resources.getIdentifier(name, defType, defPackage)

fun DatePickerDialog.getAnimatorId() = getIdentifier(SCROLL_ANIMATOR_NAME, TYPE, PACKAGE)

fun DatePickerDialog.getCalendarId() = getIdentifier(CALENDAR_VIEW_NAME, TYPE, PACKAGE)

fun DatePickerDialog.getPrevDateViewId() = getIdentifier(PREV_BUTTON_NAME, TYPE, PACKAGE)

fun DatePickerDialog.getNextDateViewId() = getIdentifier(NEXT_BUTTON_NAME, TYPE, PACKAGE)

fun DatePickerDialog.updateCalendarDate(calendar: Calendar) = apply {

    datePicker.findViewById<View>(getAnimatorId()).findViewById<View>(getCalendarId())
        .apply {
            findViewById<AppCompatImageButton>(getPrevDateViewId()).setOnClickListener {
                calendar.toPrevMonthCalendar()
                updateDate(calendar.getYear(), calendar.getMonth(), calendar.getDayOfMonth())
            }

            findViewById<AppCompatImageButton>(getNextDateViewId()).setOnClickListener {
                calendar.toNextMonthCalendar()
                updateDate(calendar.getYear(), calendar.getMonth(), calendar.getDayOfMonth())
            }
        }
}
