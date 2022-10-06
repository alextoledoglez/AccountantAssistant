package com.personal.accountantAssistant.providers

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.tasks.await

class NotificationProvider {
    private val firebaseMessaging = Firebase.messaging
    fun getToken() = flowEmit { firebaseMessaging.token.await() }
    fun subscribeToTopic(topic: String) = flowEmit {
        firebaseMessaging.subscribeToTopic(topic).await()
    }
}