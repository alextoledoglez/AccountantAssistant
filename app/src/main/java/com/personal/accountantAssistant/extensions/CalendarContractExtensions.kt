package com.personal.accountantAssistant.extensions

import android.provider.CalendarContract
import kotlin.reflect.KClass

val KClass<CalendarContract>.SELECTION_FORMAT: String get() = "((%s = ?) AND (%s = ?) AND (%s = ?) AND (%s = ?))"

val KClass<CalendarContract>.SELECTION_FIELDS: String
    get() = String.format(
        CalendarContract::class.SELECTION_FORMAT,
        *CalendarContract::class.PROJECTION
    )

val KClass<CalendarContract>.PROJECTION: Array<String>
    get() = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DESCRIPTION
    )