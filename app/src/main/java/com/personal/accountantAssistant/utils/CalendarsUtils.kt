package com.personal.accountantAssistant.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import java.util.*

@SuppressLint("MissingPermission")
object CalendarsUtils {
    private const val DEFAULT_CALENDAR_ID = 1
    private val PROJECTION = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DESCRIPTION
    )
    private val selectionFields: String
        get() {
            val selectionFormatStr = "((%s = ?) AND (%s = ?) AND (%s = ?) AND (%s = ?))"
            return String.format(selectionFormatStr, *PROJECTION)
        }

    private fun toSelectionArgs(expenseEntity: ExpenseEntity?): Array<String?> {
        return expenseEntity?.let {
            arrayOf(
                DEFAULT_CALENDAR_ID.toString(),
                it.name,
                DateUtils.toCalendarMillis(it.date).toString(),
                it.totalValue.toString()
            )
        } ?: run { emptyArray() }
    }

    private fun setRemindersFrom(contentResolver: ContentResolver, eventID: String?, minutes: Int) {
        val reminder = ContentValues()
        reminder.put(CalendarContract.Reminders.EVENT_ID, eventID?.toLong())
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        reminder.put(CalendarContract.Reminders.MINUTES, minutes)
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder)
    }

    private fun setMultipleRemindersFrom(contentResolver: ContentResolver, eventID: String?) {
        val FIVE_MINUTES = 5
        val ONE_DAY_BEFORE = 60
        val TWO_DAY_BEFORE = 1440
        setRemindersFrom(contentResolver, eventID, FIVE_MINUTES)
        setRemindersFrom(contentResolver, eventID, ONE_DAY_BEFORE)
        setRemindersFrom(contentResolver, eventID, TWO_DAY_BEFORE)
    }

    @JvmStatic
    fun createCalendarEventFrom(
        context: Context,
        expenseEntity: ExpenseEntity?
    ) {
        val contentResolver = context.contentResolver
        contentResolver?.let {
            if (expenseEntity?.isBill == true) {
                val uri =
                    if (PermissionsUtils.isCalendarWritePermissionGranted(context)) addEventFrom(
                        it,
                        expenseEntity
                    ) else null
                uri?.lastPathSegment?.let { eventID -> setMultipleRemindersFrom(it, eventID) }
            }
        }
    }

    private fun getCalendarCursorFrom(
        contentResolver: ContentResolver,
        expenseEntity: ExpenseEntity?
    ): Cursor? {
        return if (ParserUtils.isNullObject(contentResolver)) null else contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            PROJECTION,
            selectionFields,
            toSelectionArgs(expenseEntity),
            null
        )
    }

    private fun alreadyExistCalendarEventFor(
        contentResolver: ContentResolver,
        expenseEntity: ExpenseEntity?
    ): Boolean {
        getCalendarCursorFrom(contentResolver, expenseEntity)?.let {
            readCalendarEvent(contentResolver, expenseEntity)
            return java.lang.Boolean.TRUE
        } ?: run { return java.lang.Boolean.FALSE }
    }

    private fun addEventFrom(
        contentResolver: ContentResolver,
        expenseEntity: ExpenseEntity?
    ): Uri? {
        val timeZone = TimeZone.getDefault()
        val calendarMillis = DateUtils.toCalendarMillis(expenseEntity?.date)
        val event = ContentValues()
        event.put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID)
        event.put(CalendarContract.Events.ALL_DAY, java.lang.Boolean.TRUE)
        event.put(CalendarContract.Events.STATUS, java.lang.Boolean.TRUE)
        event.put(CalendarContract.Events.HAS_ALARM, 1)
        event.put(CalendarContract.Events.TITLE, expenseEntity?.name)
        event.put(CalendarContract.Events.DTSTART, calendarMillis)
        event.put(CalendarContract.Events.DTEND, calendarMillis)
        //TODO event.put("rrule", "FREQ=YEARLY");
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.id)
        event.put(CalendarContract.Events.DESCRIPTION, expenseEntity?.totalValue)
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        event.put(CalendarContract.EXTRA_EVENT_ALL_DAY, java.lang.Boolean.TRUE)
        //event.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarMillis);
        //event.put(CalendarContract.EXTRA_EVENT_END_TIME, calendarMillis);
        var uri: Uri? = null
        if (alreadyExistCalendarEventFor(contentResolver, expenseEntity)) {
            contentResolver.update(
                CalendarContract.Events.CONTENT_URI,
                event,
                selectionFields,
                null
            )
        } else {
            uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
        }
        return uri
    }

    @JvmStatic
    fun deleteCalendarEventsFrom(
        context: Context,
        expenseEntity: ExpenseEntity?
    ) {
        if (PermissionsUtils.isCalendarWritePermissionGranted(context)) {
            val contentResolver = context.contentResolver
            if (alreadyExistCalendarEventFor(contentResolver, expenseEntity)) {
                contentResolver.delete(
                    CalendarContract.Events.CONTENT_URI,
                    selectionFields,
                    toSelectionArgs(expenseEntity)
                )
            }
        }
    }

    private fun clearAllCalendarEvents(contentResolver: ContentResolver): Int {
        var result = -1
        if (!ParserUtils.isNullObject(contentResolver)) {
            result = contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events.CALENDAR_ID + " = ?",
                arrayOf(DEFAULT_CALENDAR_ID.toString())
            )
        }
        return result
    }

    private fun readCalendarEvent(
        contentResolver: ContentResolver,
        expenseEntity: ExpenseEntity?
    ) {
        //TODO
        print(expenseEntity)
        contentResolver.query(CalendarContract.Events.CONTENT_URI, PROJECTION, null, null, null)
            ?.apply {
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
}