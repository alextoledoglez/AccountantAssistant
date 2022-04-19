package com.personal.accountantAssistant.extensions

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import com.personal.accountantAssistant.data.mappers.toCalendarSelectionArgs
import com.personal.accountantAssistant.domain.models.ExpenseModel
import java.util.*

private fun ContentResolver?.getAllCalendarEvents() = this?.query(
    CalendarContract.Events.CONTENT_URI,
    CalendarContract::class.PROJECTION,
    null,
    null,
    null
)

private fun ContentResolver?.getCalendarCursorFrom(model: ExpenseModel?) = this?.query(
    CalendarContract.Events.CONTENT_URI,
    CalendarContract::class.PROJECTION,
    CalendarContract::class.SELECTION_FIELDS,
    model.toCalendarSelectionArgs(),
    null
)

private fun ContentResolver.readCalendarEvent(model: ExpenseModel?) {
    //TODO
    print(model)
    getAllCalendarEvents()?.apply {
        var strCalendarValues: StringBuilder? = null
        moveToFirst()
        val arrayCalendarValues = arrayOfNulls<String>(count)
        for (i in 0 until count) {
            arrayCalendarValues[i] = """
                    Event${getInt(0)}: 
                    Title: ${getString(1)}
                    Start Date: ${Date(getLong(2))}
                    Description: ${getString(3)}
                    """.trimIndent()

            strCalendarValues = strCalendarValues?.append(arrayCalendarValues[i])
                ?: arrayCalendarValues[i]?.let { StringBuilder(it) }

            moveToNext()
        }
        close()
    }
}

private fun ContentResolver?.updateCalendarEvent(event: ContentValues) = this?.update(
    CalendarContract.Events.CONTENT_URI,
    event,
    CalendarContract::class.SELECTION_FIELDS,
    null
)

private fun ContentResolver?.insertCalendarEvent(
    event: ContentValues
) = this?.insert(CalendarContract.Events.CONTENT_URI, event)

private fun ContentResolver?.clearAllCalendarEvents() = this?.delete(
    CalendarContract.Events.CONTENT_URI,
    CalendarContract.Events.CALENDAR_ID + " = ?",
    arrayOf(Int.DEFAULT_CALENDAR_ID.toString())
).orValue(value = -1)

private fun ContentResolver.setReminder(eventID: String?, minutes: Int) {
    val reminder = ContentValues().apply {
        put(CalendarContract.Reminders.EVENT_ID, eventID?.toLong())
        put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        put(CalendarContract.Reminders.MINUTES, minutes)
    }
    insert(CalendarContract.Reminders.CONTENT_URI, reminder)
}

fun ContentResolver?.alreadyExistCalendarEventFor(
    model: ExpenseModel?
) = getCalendarCursorFrom(model)?.count.isMoreThanZero()

fun ContentResolver.addEvent(model: ExpenseModel?): Uri? {
    val timeZone = TimeZone.getDefault()
    val calendarMillis = model?.date.toCalendarMillis()
    val event = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, Int.DEFAULT_CALENDAR_ID)
        put(CalendarContract.Events.ALL_DAY, java.lang.Boolean.TRUE)
        put(CalendarContract.Events.STATUS, java.lang.Boolean.TRUE)
        put(CalendarContract.Events.HAS_ALARM, 1)
        put(CalendarContract.Events.TITLE, model?.name)
        put(CalendarContract.Events.DTSTART, calendarMillis)
        put(CalendarContract.Events.DTEND, calendarMillis)
        //TODO put("rrule", "FREQ=YEARLY");
        put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)
        put(
            CalendarContract.Events.DESCRIPTION,
            model?.totalValue.orZero().toDouble()
        )
        put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        put(CalendarContract.EXTRA_EVENT_ALL_DAY, java.lang.Boolean.TRUE)
        //put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarMillis);
        //put(CalendarContract.EXTRA_EVENT_END_TIME, calendarMillis);
    }

    var uri: Uri? = null
    if (alreadyExistCalendarEventFor(model))
        updateCalendarEvent(event)
    else
        uri = insertCalendarEvent(event)
    return uri
}

fun ContentResolver.setMultipleReminders(eventID: String?) {
    setReminder(eventID, minutes = 5)
    setReminder(eventID, minutes = 60)
    setReminder(eventID, minutes = 1440)
}