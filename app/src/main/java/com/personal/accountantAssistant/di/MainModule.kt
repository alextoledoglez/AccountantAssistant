package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.ui.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
}

val utilsModule = module {
    single { LocalStorage(get()) }
}

object MainModuleInitializer {
    fun initialize() = loadKoinModules(listOf(viewModelModule, utilsModule))
}