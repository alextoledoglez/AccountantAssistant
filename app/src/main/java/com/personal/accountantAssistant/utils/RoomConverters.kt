package com.personal.accountantAssistant.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.*


class RoomConverters {
    private val cardsType: Type = object : TypeToken<List<CardEntity?>?>() {}.type
    private val expensesType: Type = object : TypeToken<List<ExpenseEntity?>?>() {}.type

    @TypeConverter
    fun toDate(strDate: String?): Date? = DateUtils.toDate(strDate)

    @TypeConverter
    fun toString(date: Date?): String? = DateUtils.toString(date)

    @TypeConverter
    fun toString(value: BigDecimal) = value.toString()

    @TypeConverter
    fun toBigDecimal(value: String) = value.toBigDecimal()

    @TypeConverter
    fun fromCardString(value: String?): List<CardEntity?>? = Gson().fromJson(value, cardsType)

    @TypeConverter
    fun fromCards(list: List<CardEntity?>?): String? = Gson().toJson(list)

    @TypeConverter
    fun fromExpenseString(value: String?): List<ExpenseEntity?>? = Gson().fromJson(
        value, expensesType
    )

    @TypeConverter
    fun fromExpenses(expenses: List<ExpenseEntity?>?): String? = Gson().toJson(expenses)
}