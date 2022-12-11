package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.services.NotificationService
import com.personal.accountantAssistant.services.SignInService
import org.koin.dsl.module

object ServicesModule {

    fun getServices() = module {
        single { NotificationService() }
        single { SignInService(get(), get(), get(), get()) }
    }
}