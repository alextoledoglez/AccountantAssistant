package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.providers.*
import org.koin.dsl.module

object ProvidersModule {

    fun getProviders() = module {
        single { AnalyticsProvider() }
        single { CrashlyticsProvider() }
        single { AdProvider(get(), get()) }
        single { NotificationProvider() }
        single { RemoteConfigProvider() }
    }
}