package com.personal.accountantAssistant

import android.app.Application
import com.personal.accountantAssistant.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class AccountantAssistant : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AccountantAssistant)
            loadKoinModules(MainModule.getModules())
        }
    }
}