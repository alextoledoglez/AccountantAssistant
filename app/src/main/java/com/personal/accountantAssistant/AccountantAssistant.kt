package com.personal.accountantAssistant

import android.app.Application
import com.personal.accountantAssistant.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class AccountantAssistant : Application() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate() {
        super.onCreate()
        val context = this@AccountantAssistant
        startKoin {
            androidLogger()
            androidContext(context)
            workManagerFactory()
            loadKoinModules(MainModule.getModules())
        }
    }
}