package com.personal.accountantAssistant.data

import android.content.Context
import android.content.SharedPreferences
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.*
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass

class LocalStorage(val context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}_preferences", Context.MODE_PRIVATE
    )

    private fun <T : Any> getObject(key: String, clazz: KClass<T>): T? {
        return preferences.getString(key, null)?.fromJson(clazz)
    }

    private fun <T : Any> putObject(key: String, obj: T) {
        preferences.edit().putString(key, obj.toJson()).apply()
    }

    fun setAvailableMoney(availableMoneyValue: Float) {
        preferences.edit().putFloat(AVAILABLE_MONEY, availableMoneyValue).apply()
    }

    fun getAvailableMoney(): BigDecimal = preferences.getFloat(
        AVAILABLE_MONEY, BigDecimal.ZERO.toFloat()
    ).toBigDecimal().rounded()

    private fun setFirstStrDate(firstDate: Date?) {
        preferences.edit().putString(FIRST_STR_DATE, firstDate.toDateStr()).apply()
    }

    fun getFirstDate() = preferences.getString(FIRST_STR_DATE, String.EMPTY)?.toDate().orCurrent()

    private fun setLastStrDate(lastDate: Date?) {
        preferences.edit().putString(LAST_STR_DATE, lastDate.toDateStr()).apply()
    }

    fun getLastDate(): Date = preferences.getString(LAST_STR_DATE, String.EMPTY)?.toDate() ?: run {
        val nextMonth = Calendar.getInstance()
        nextMonth.add(Calendar.MONTH, Int.ONE)
        nextMonth.time
    }

    fun setPeriodDates(firstDate: Date?, lastDate: Date?) {
        setFirstStrDate(firstDate)
        setLastStrDate(lastDate)
    }

    fun setSignedUser(user: UserModel?) {
        putObject(SIGNED_USER, user ?: UserModel())
    }

    fun getSignedUser() = getObject(SIGNED_USER, UserModel::class)

    companion object {
        private const val AVAILABLE_MONEY = "available_money"
        private const val FIRST_STR_DATE = "first_str_date"
        private const val LAST_STR_DATE = "last_str_date"
        private const val SIGNED_USER = "signed_user"
    }
}