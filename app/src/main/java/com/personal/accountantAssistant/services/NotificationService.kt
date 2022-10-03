package com.personal.accountantAssistant.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
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

    enum class ListNotificationType(val id: Int) {
        Summary(id = NOTIFICATION_ID)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        analytics?.trackOnNewToken(token)
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        analytics?.trackOnMessageReceived(remoteMessage.from)
        remoteMessage.notification?.let { showNotification(it) }
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        analytics?.trackSendRegistrationToServer(token)
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        analytics?.trackNotificationMessage(notification.title, notification.body)
        createNotification(notification.title.orEmpty(), notification.body.orEmpty())
    }

    private fun createChannel(id: String, name: String, importance: Int): NotificationChannel {
        return NotificationChannel(id, name, importance).apply {
            setSound(null, null)
            setShowBadge(false)
        }
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = ArrayList<NotificationChannel>()
            channels.add(createChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_LOW))
            notificationManager.createNotificationChannels(channels)
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

    fun createNotification(title: String, description: String) {
        createChannels()
        notificationManager.notify(
            ListNotificationType.Summary.id,
            defaultBuilder(title, description, groupOrServiceKey = GROUP_KEY).build()
        )
    }

    fun getNotification(): Notification {
        createChannels()
        return defaultBuilder(groupOrServiceKey = SERVICE_KEY).build()
    }

    fun clearNotification() {
        ListNotificationType.values().forEach { notificationManager.cancel(it.id) }
    }

    companion object {
        private val TAG = NotificationService::class.java.simpleName

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "AccountantAssistantService Kotlin"
        private const val CHANNEL_NAME = "Accountant Assistant"
        private const val GROUP_KEY = "com.personal.accountantAssistant.notification"
        private const val SERVICE_KEY = "com.personal.accountantAssistant.notification.service"
    }
}
