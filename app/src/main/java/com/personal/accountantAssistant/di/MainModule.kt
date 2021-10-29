package com.personal.accountantAssistant.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { BuysViewModel() }
    viewModel { BillsViewModel() }
}

@RequiresApi(Build.VERSION_CODES.P)
val utilsModule = module {
    single { LocalStorage(get()) }
    single { DatabaseManager(androidContext()) }
}

object MainModuleInitializer {
    fun initialize() = loadKoinModules(listOf(viewModelModule, utilsModule))
}