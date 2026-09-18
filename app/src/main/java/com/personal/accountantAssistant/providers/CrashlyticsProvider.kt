package com.personal.accountantAssistant.providers

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class CrashlyticsProvider {

    private val instance = Firebase.crashlytics

    fun setUser(userName: String?) {
        if (userName.isNullOrBlank()) {
            clearUser()
        } else {
            instance.setUserId(ANONYMOUS_USER_ID)
        }
    }

    fun clearUser() {
        instance.setUserId("")
    }

    companion object {
        private const val ANONYMOUS_USER_ID = "authenticated_user"
    }
}
