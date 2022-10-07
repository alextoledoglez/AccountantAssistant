package com.personal.accountantAssistant.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.extensions.EMPTY
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
        remoteMessage.notification?.let { showNotification(it) }
    }

    private fun sendRegistrationToServer(token: String?) {
        trackNotificationEvent(event = "sendRegistrationTokenToServer", value = token.orEmpty())
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val title = notification.title.orEmpty()
        val body = notification.body.orEmpty()
        trackNotificationEvent(event = "showNotification", value = "title: $title and body: $body")
        createNotification(title, body)
    }

    private fun createChannel(importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                setSound(null, null)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun defaultBuilder(
        title: String = String.EMPTY,
        description: String = String.EMPTY,
        groupOrServiceKey: String
    ) = NotificationCompat.Builder(context, CHANNEL_ID).apply {
        setSmallIcon(R.mipmap.ic_launcher)
        setContentTitle(title)
        setContentText(description)
        setShowWhen(false)
        setOngoing(true)
        setAutoCancel(false)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setGroup(groupOrServiceKey)
    }

    private fun trackNotificationEvent(event: String, value: String) {
        analytics?.trackEvent(TAG, event, value)
    }

    fun getPendingIntent(): PendingIntent {
        createChannel(IMPORTANCE_DEFAULT)
        val intent = Intent(context, NotificationService::class.java).apply {
            putExtra(TITLE_EXTRA, "Expiring Bills")
            putExtra(MESSAGE_EXTRA, "You have Bills expiring today!")
        }
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, flags)
    }

    fun createNotification(title: String, description: String) {
        createChannel(IMPORTANCE_LOW)
        notificationManager.notify(
            NOTIFICATION_ID,
            defaultBuilder(title, description, groupOrServiceKey = GROUP_KEY).build()
        )
    }

    fun getNotification(): Notification {
        createChannel(IMPORTANCE_LOW)
        return defaultBuilder(groupOrServiceKey = SERVICE_KEY).build()
    }

    fun clearNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private val TAG = NotificationService::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val TITLE_EXTRA = "TITLE_EXTRA"
        const val MESSAGE_EXTRA = "MESSAGE_EXTRA"
        const val CHANNEL_ID = "AccountantAssistantNotificationService"
        private const val CHANNEL_NAME = "Accountant Assistant"
        private const val GROUP_KEY = "com.personal.accountantAssistant.notification"
        private const val SERVICE_KEY = "com.personal.accountantAssistant.notification.service"
    }
}
