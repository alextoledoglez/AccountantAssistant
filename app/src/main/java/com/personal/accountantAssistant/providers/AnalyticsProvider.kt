package com.personal.accountantAssistant.providers

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.personal.accountantAssistant.domain.models.UserModel

class AnalyticsProvider {

    private val instance = Firebase.analytics

    private fun Map<String, Any?>?.toEventBundle() = Bundle().apply {
        this@toEventBundle?.forEach { (key, value) ->
            putString(key, value?.toString().orEmpty())
        }
    }

    fun trackEvent(type: String, key: String, value: String?) {
        instance.logEvent(type, mapOf(key to value.orEmpty()).toEventBundle())
    }

    /**
     * Keeps analytics detached from personally identifiable account data.
     *
     * The parameter remains part of the API so callers do not need behavioral changes.
     */
    fun setUserAccount(user: UserModel?) {
        if (user != null) {
            instance.setUserId(null)
        }
    }

    fun trackErrorEvent(error: String?) {
        val status = if (error.isNullOrBlank()) "unknown_error" else "reported_error"
        trackEvent(ERROR_KEY, ERROR_MESSAGE_KEY, status)
    }

    fun trackScreenViewEvent(className: String?) {
        trackEvent(FirebaseAnalytics.Event.SCREEN_VIEW, CLASS_NAME_KEY, className.orEmpty())
    }

    companion object {
        const val ERROR_KEY = "ERROR_KEY"
        const val CLASS_NAME_KEY = "CLASS_NAME_KEY"
        const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE_KEY"
    }
}
