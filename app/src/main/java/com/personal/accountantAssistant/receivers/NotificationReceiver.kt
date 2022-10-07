package com.personal.accountantAssistant.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.services.NotificationService
import org.koin.java.KoinJavaComponent.getKoin

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(intent.getStringExtra(NotificationService.TITLE_EXTRA))
            .setContentText(intent.getStringExtra(NotificationService.MESSAGE_EXTRA))
            .build()
        getKoin()
            .get<NotificationManager>()
            .notify(NotificationService.NOTIFICATION_ID, notification)
    }
}