package com.personal.accountantAssistant.providers

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import com.personal.accountantAssistant.extensions.flowEmit

class NotificationProvider {
    private val firebaseMessaging = Firebase.messaging
    fun getToken() = flowEmit { firebaseMessaging.token.await() }
}