package com.personal.accountantAssistant.data

import android.content.Context
import com.personal.accountantAssistant.bases.BasePreferences
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toSharedPreferences
import java.math.BigDecimal

class LocalStorage(val context: Context) : BasePreferences(context.toSharedPreferences()) {

    fun setNotificationToken(token: String?) = flowEmit {
        putObject(NOTIFICATION_TOKEN, token)
    }

    fun getNotificationToken() = flowEmit {
        getString(NOTIFICATION_TOKEN, String.EMPTY)
    }

    fun getAvailableMoney() = flowEmit {
        getFloat(AVAILABLE_MONEY, BigDecimal.ZERO.toFloat())
            .toBigDecimal()
            .rounded()
    }

    companion object {
        const val AVAILABLE_MONEY = "AVAILABLE_MONEY"
        const val FIRST_STR_DATE = "FIRST_STR_DATE"
        const val LAST_STR_DATE = "LAST_STR_DATE"
        const val SIGNED_USER = "SIGNED_USER"
        const val NOTIFICATION_TOKEN = "NOTIFICATION_TOKEN"
    }
}