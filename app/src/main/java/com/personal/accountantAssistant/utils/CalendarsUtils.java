package com.personal.accountantAssistant.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.personal.accountantAssistant.ui.payments.entities.Payments;

import java.util.Date;
import java.util.TimeZone;

@SuppressLint("MissingPermission")
public class CalendarsUtils {

    private final static int DEFAULT_CALENDAR_ID = 1;

    private final static String[] PROJECTION = new String[]{
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DESCRIPTION
    };

    private static String getSelectionFields() {
        final String selectionFormatStr = "((%s = ?) AND (%s = ?) AND (%s = ?) AND (%s = ?))";
        return String.format(selectionFormatStr, (Object[]) PROJECTION);
    }

    private static String[] toSelectionArgs(final Payments payments) {
        return new String[]{
                String.valueOf(DEFAULT_CALENDAR_ID),
                payments.getName(),
                String.valueOf(DateUtils.toCalendarMillis(payments.getDate())),
                String.valueOf(payments.getTotalValue())
        };
    }

    private static void setRemindersFrom(final ContentResolver contentResolver,
                                         final String eventID,
                                         final int minutes) {
        final ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(eventID));
        reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminder.put(CalendarContract.Reminders.MINUTES, minutes);
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder);
    }

    private static void setMultipleRemindersFrom(final ContentResolver contentResolver,
                                                 final String eventID) {
        final int FIVE_MINUTES = 5;
        final int ONE_DAY_BEFORE = 60;
        final int TWO_DAY_BEFORE = 1440;
        setRemindersFrom(contentResolver, eventID, FIVE_MINUTES);
        setRemindersFrom(contentResolver, eventID, ONE_DAY_BEFORE);
        setRemindersFrom(contentResolver, eventID, TWO_DAY_BEFORE);
    }

    static void createCalendarEventFrom(final Context context,
                                        final Payments payments) {

        final ContentResolver contentResolver = context.getContentResolver();

        if (!ParserUtils.isNullObject(contentResolver) && payments.isBill()) {

            final Uri uri = PermissionsUtils.isCalendarWritePermissionGranted(context) ?
                    addEventFrom(contentResolver, payments) :
                    null;

            if (!ParserUtils.isNullObject(uri)) {
                final String eventID = uri.getLastPathSegment();
                if (!ParserUtils.isNullObject(eventID)) {
                    setMultipleRemindersFrom(contentResolver, eventID);
                }
            }
        }
    }

    private static Cursor getCalendarCursorFrom(final ContentResolver contentResolver,
                                                final Payments payments) {
        return ParserUtils.isNullObject(contentResolver) ?
                null :
                contentResolver.query(CalendarContract.Events.CONTENT_URI,
                        PROJECTION,
                        getSelectionFields(),
                        toSelectionArgs(payments),
                        null);
    }

    private static boolean alreadyExistCalendarEventFor(final ContentResolver contentResolver,
                                                        final Payments payments) {

        final Cursor cursor = getCalendarCursorFrom(contentResolver, payments);

        if (!ParserUtils.isNullObject(cursor) && cursor.getCount() > 0) {
            readCalendarEvent(contentResolver, payments);
            //clearAllCalendarEvents(contentResolver);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private static Uri addEventFrom(final ContentResolver contentResolver,
                                    final Payments payments) {

        final TimeZone timeZone = TimeZone.getDefault();
        final long calendarMillis = DateUtils.toCalendarMillis(payments.getDate());

        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID);
        event.put(CalendarContract.Events.ALL_DAY, Boolean.TRUE);
        event.put(CalendarContract.Events.STATUS, Boolean.TRUE);
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        event.put(CalendarContract.Events.TITLE, payments.getName());
        event.put(CalendarContract.Events.DTSTART, calendarMillis);
        event.put(CalendarContract.Events.DTEND, calendarMillis);
        //TODO event.put("rrule", "FREQ=YEARLY");
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        event.put(CalendarContract.Events.DESCRIPTION, payments.getTotalValue());
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        event.put(CalendarContract.EXTRA_EVENT_ALL_DAY, Boolean.TRUE);
        //event.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarMillis);
        //event.put(CalendarContract.EXTRA_EVENT_END_TIME, calendarMillis);

        Uri uri = null;

        if (alreadyExistCalendarEventFor(contentResolver, payments)) {
            contentResolver.update(CalendarContract.Events.CONTENT_URI, event, getSelectionFields(), null);
        } else {
            uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, event);
        }

        return uri;
    }

    public static void deleteCalendarEventsFrom(final Context context,
                                                final Payments payments) {
        if (PermissionsUtils.isCalendarWritePermissionGranted(context)) {
            final ContentResolver contentResolver = context.getContentResolver();
            if (alreadyExistCalendarEventFor(contentResolver, payments)) {
                contentResolver.delete(CalendarContract.Events.CONTENT_URI, getSelectionFields(), toSelectionArgs(payments));
            }
        }
    }

    private static int clearAllCalendarEvents(final ContentResolver contentResolver) {
        int result = -1;
        if (!ParserUtils.isNullObject(contentResolver)) {
            result = contentResolver.delete(CalendarContract.Events.CONTENT_URI,
                    CalendarContract.Events.CALENDAR_ID + " = ?",
                    new String[]{String.valueOf(DEFAULT_CALENDAR_ID)});
        }
        return result;
    }

    private static void readCalendarEvent(final ContentResolver contentResolver,
                                          final Payments payments) {

        /*final Cursor cursor = getCalendarCursorFrom(contentResolver, payments);*/
        final Cursor cursor = ParserUtils.isNullObject(contentResolver) ?
                null :
                contentResolver.query(CalendarContract.Events.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null);

        if (!ParserUtils.isNullObject(cursor)) {

            StringBuilder strCalendarValues = null;
            cursor.moveToFirst();
            final int cursorSize = cursor.getCount();
            String[] arrayCalendarValues = new String[cursorSize];

            for (int i = 0; i < cursorSize; i++) {

                arrayCalendarValues[i] = "Event" + cursor.getInt(0) +
                        ": \nTitle: " + cursor.getString(1) +
                        "\nStart Date: " + new Date(cursor.getLong(2)) +
                        "\nDescription: " + cursor.getString(3);

                if (strCalendarValues == null)
                    strCalendarValues = new StringBuilder(arrayCalendarValues[i]);
                else {
                    strCalendarValues.append(arrayCalendarValues[i]);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
