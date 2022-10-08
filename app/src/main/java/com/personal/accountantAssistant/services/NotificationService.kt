package com.personal.accountantAssistant.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.providers.AnalyticsProvider
import org.koin.android.ext.android.inject

class NotificationService : FirebaseMessagingService() {

    private val context: Context by inject()
    private val analytics: AnalyticsProvider? by inject()
    private val notificationManager: NotificationManager by inject()

    override fun onNewToken(token: String) {
        trackNotificationEvent(event = "onNewToken", value = token)
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        trackNotificationEvent(event = "onMessageReceived", value = "${remoteMessage.from}")
        remoteMessage.notification?.let { showNotification(it.title.orEmpty(), it.body.orEmpty()) }
    }

    private fun sendRegistrationToServer(token: String?) {
        trackNotificationEvent(event = "sendRegistrationTokenToServer", value = token.orEmpty())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT).apply {
                setSound(null, null)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificationBuilder(title: String, description: String) =
        NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(description)
            setShowWhen(false)
            setAutoCancel(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setGroup(GROUP_KEY)
        }

    private fun trackNotificationEvent(event: String, value: String) {
        analytics?.trackEvent(TAG, event, value)
    }

    fun clearNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun showNotification(title: String, text: String) {
        createChannel()
        val notification = notificationBuilder(title, text).build()
        trackNotificationEvent(event = "showNotification", value = "title: $title and text: $text")
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private val TAG = NotificationService::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "AccountantAssistantNotificationService"
        private const val CHANNEL_NAME = "Accountant Assistant"
        private const val GROUP_KEY = "com.personal.accountantAssistant.notification"
    }
}
