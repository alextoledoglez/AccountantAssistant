package com.personal.accountantAssistant.core.extensions

import android.database.Cursor

fun Cursor.getIntColumn(column: String) = getInt(getColumnIndex(column))
fun Cursor.getDoubleColumn(column: String): Double = getDouble(getColumnIndex(column))
fun Cursor.getStringColumn(column: String): String? = getString(getColumnIndex(column))