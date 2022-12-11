package com.personal.accountantAssistant.di

import android.app.NotificationManager
import android.content.Context
import org.koin.dsl.module

object ManagersModule {

    fun getManagers() = module {
        single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    }
}