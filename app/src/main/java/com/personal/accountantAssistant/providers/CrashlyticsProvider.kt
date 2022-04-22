package com.personal.accountantAssistant.providers

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class CrashlyticsProvider {

    private val instance = Firebase.crashlytics

    fun setUser(userName: String?) {
        instance.setUserId(userName.orEmpty())
    }
}