package com.personal.accountantAssistant.data

import android.content.Context
import com.personal.accountantAssistant.bases.BasePreferences
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.getNextMonthDate
import com.personal.accountantAssistant.extensions.orCurrent
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toDate
import com.personal.accountantAssistant.extensions.toDateStr
import com.personal.accountantAssistant.extensions.toSharedPreferences
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class LocalStorage(val context: Context) : BasePreferences(context.toSharedPreferences()) {

    fun setNotificationToken(token: String?) = flowEmit {
        putObject(NOTIFICATION_TOKEN, token)
    }

    fun getNotificationToken() = flowEmit {
        getString(NOTIFICATION_TOKEN, String.EMPTY)
    }

    fun setAvailableMoney(value: Float?) = flowEmit {
        edit().putFloat(AVAILABLE_MONEY, value.orZero()).apply()
    }

    fun getAvailableMoney() = flowEmit {
        getFloat(AVAILABLE_MONEY, BigDecimal.ZERO.toFloat())
            .toBigDecimal()
            .rounded()
    }

    fun setFirstDate(value: Date?) = flowEmit {
        edit().putString(FIRST_STR_DATE, value.toDateStr()).apply()
    }

    fun getFirstDate() = getString(FIRST_STR_DATE, String.EMPTY)?.toDate().orCurrent()

    fun setLastDate(value: Date?) = flowEmit {
        edit().putString(LAST_STR_DATE, value.toDateStr()).apply()
    }

    fun getSecondDate(): Date? = getString(LAST_STR_DATE, String.EMPTY)?.toDate() ?: run {
        Calendar.getInstance().getNextMonthDate()
    }

    fun setPeriodDates(first: Date?, last: Date?) = flowEmit {
        edit().apply {
            putString(FIRST_STR_DATE, first.toDateStr())
            putString(LAST_STR_DATE, last.toDateStr())
        }.apply()
    }

    fun setSignedUser(user: UserModel?) = flowEmit {
        putObject(SIGNED_USER, user)
    }

    fun getSignedUser() = flowEmit {
        getObject(SIGNED_USER, UserModel::class)
    }

    companion object {
        private const val AVAILABLE_MONEY = "AVAILABLE_MONEY"
        private const val FIRST_STR_DATE = "FIRST_STR_DATE"
        private const val LAST_STR_DATE = "LAST_STR_DATE"
        private const val SIGNED_USER = "SIGNED_USER"
        private const val NOTIFICATION_TOKEN = "NOTIFICATION_TOKEN"
    }
}