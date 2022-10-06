package com.personal.accountantAssistant.workers

import android.content.Context
import androidx.work.*
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.services.NotificationService
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

@OptIn(KoinApiExtension::class)
class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val analytics: AnalyticsProvider,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun doWork(): Result {
        val title = "Hello"
        val description = "Accountant Assistant!"
        analytics.trackEvent(
            TAG,
            key = WORK_EXECUTION_TRACK_KEY,
            value = "createNotification with: title=$title and description=$description"
        )
        notificationService.createNotification(title, description)
        return Result.success()
    }

    private fun periodicWorkBuilder() = PeriodicWorkRequestBuilder<NotificationWorker>(
        REPEAT_INTERVAL,
        TimeUnit.MINUTES,
        FLEX_INTERVAL,
        TimeUnit.MINUTES
    )

    private fun buildConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private fun clearPeriodicWork(): Operation = workManager.cancelAllWorkByTag(TAG)

    fun setupPeriodicWork(): Operation {
        clearPeriodicWork()
        analytics.trackEvent(
            TAG,
            key = WORK_SETUP_TRACK_KEY,
            value = "enqueue with repeat=$REPEAT_INTERVAL and flex=$FLEX_INTERVAL intervals"
        )
        return workManager.enqueue(
            periodicWorkBuilder().addTag(TAG).setConstraints(buildConstraints()).build()
        )
    }

    companion object {
        val TAG = NotificationWorker::class.simpleName.orEmpty()
        private const val WORK_SETUP_TRACK_KEY = "setupPeriodicWork"
        private const val WORK_EXECUTION_TRACK_KEY = "doWork"
        private const val REPEAT_INTERVAL = 15L
        private const val FLEX_INTERVAL = 5L
    }
}