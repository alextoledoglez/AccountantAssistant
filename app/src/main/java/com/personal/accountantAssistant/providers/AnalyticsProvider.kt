package com.personal.accountantAssistant.providers

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsProvider {

    private val instance = Firebase.analytics

    private fun Map<String, Any?>?.toEventBundle() = Bundle().apply {
        this@toEventBundle?.forEach { putString(it.key, it.value.toString()) }
    }

    private fun trackEvent(type: String, key: String, value: String?) {
        instance.logEvent(type, mapOf(key to value.orEmpty()).toEventBundle())
    }

    fun setUserEmail(userEmail: String?) {
        instance.setUserProperty(EMAIL_KEY, userEmail)
    }

    fun trackErrorEvent(error: String?) {
        trackEvent(ERROR_KEY, ERROR_MESSAGE_KEY, error.orEmpty())
    }

    companion object {
        const val EMAIL_KEY = "EMAIL_KEY"
        const val ERROR_KEY = "ERROR_KEY"
        const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE_KEY"
    }
}