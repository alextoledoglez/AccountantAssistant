package com.personal.accountantAssistant.providers

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.personal.accountantAssistant.domain.models.UserModel

class AnalyticsProvider {

    private val instance = Firebase.analytics

    private fun Map<String, Any?>?.toEventBundle() = Bundle().apply {
        this@toEventBundle?.forEach {
            putString(it.key, it.value.toString())
            Log.d(it.key, it.value.toString())
        }
    }

    fun trackEvent(type: String, key: String, value: String?) {
        instance.logEvent(type, mapOf(key to value.orEmpty()).toEventBundle())
    }

    fun setUserAccount(user: UserModel?) {
        user?.apply {
            instance.setUserProperty(UserModel.USER_DISPLAY_NAME_KEY, name)
            instance.setUserProperty(UserModel.USER_FULL_NAME_KEY, fullName)
            instance.setUserProperty(UserModel.USER_EMAIL_KEY, email)
        }
    }

    fun trackErrorEvent(error: String?) {
        trackEvent(ERROR_KEY, ERROR_MESSAGE_KEY, error.orEmpty())
    }

    fun trackScreenViewEvent(className: String?) {
        trackEvent(FirebaseAnalytics.Event.SCREEN_VIEW, CLASS_NAME_KEY, className.orEmpty())
    }

    fun trackOnMessageReceived(from: String?) {
        trackEvent(NOTIFICATION_KEY, MESSAGE_RECEIVED_FROM, from.orEmpty())
    }

    fun trackOnNewToken(token: String?) {
        trackEvent(NOTIFICATION_KEY, ON_NEW_TOKEN, token.orEmpty())
    }

    fun trackSendRegistrationToServer(token: String?) {
        trackEvent(NOTIFICATION_KEY, SEND_REGISTRATION_TO_SERVER, token.orEmpty())
    }

    fun trackNotificationMessage(title: String?, text: String?) {
        trackEvent(NOTIFICATION_KEY, MESSAGE_TITLE, title.orEmpty())
        trackEvent(NOTIFICATION_KEY, MESSAGE_TEXT, text.orEmpty())
    }

    companion object {
        const val ERROR_KEY = "ERROR_KEY"
        const val CLASS_NAME_KEY = "CLASS_NAME_KEY"
        const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE_KEY"
        const val NOTIFICATION_KEY = "NOTIFICATION"
        const val ON_NEW_TOKEN = "ON_NEW_TOKEN"
        const val MESSAGE_TITLE = "MESSAGE_TITLE"
        const val MESSAGE_TEXT = "MESSAGE_TEXT"
        const val MESSAGE_RECEIVED_FROM = "MESSAGE_RECEIVED_FROM"
        const val SEND_REGISTRATION_TO_SERVER = "SEND_REGISTRATION_TO_SERVER"
    }
}