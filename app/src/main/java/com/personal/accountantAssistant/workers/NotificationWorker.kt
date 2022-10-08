package com.personal.accountantAssistant.workers

import android.content.Context
import androidx.work.*
import com.personal.accountantAssistant.domain.useCases.GetBillsDueSoonUseCase
import com.personal.accountantAssistant.domain.useCases.GetBillsDueTodayUseCase
import com.personal.accountantAssistant.extensions.isMoreThanZero
import com.personal.accountantAssistant.services.NotificationService
import kotlinx.coroutines.flow.singleOrNull
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

@OptIn(KoinApiExtension::class)
class NotificationWorker(
    val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val getBillsDueSoonUseCase: GetBillsDueSoonUseCase by inject()
    private val getBillsDueTodayUseCase: GetBillsDueTodayUseCase by inject()
    private val notificationService: NotificationService by inject()

    override suspend fun doWork(): Result {
        when {
            getBillsDueTodayUseCase().singleOrNull()?.size.isMoreThanZero() ->
                notificationService.showBillsDueTodayNotification()

            getBillsDueSoonUseCase().singleOrNull()?.size.isMoreThanZero() ->
                notificationService.showBillsDueSoonNotification()
        }
        return Result.success()
    }

    companion object {

        private val TAG = NotificationWorker::class.simpleName.orEmpty()
        private const val INTERVAL = 3L

        fun setupPeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<NotificationWorker>(INTERVAL, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, request)
        }
    }
}