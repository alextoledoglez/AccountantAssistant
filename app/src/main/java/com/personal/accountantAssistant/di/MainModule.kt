package com.personal.accountantAssistant.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.data.remote.ExpensesRemoteDataSource
import com.personal.accountantAssistant.data.repository.CardsDataRepository
import com.personal.accountantAssistant.data.repository.ExpensesDataRepository
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { WalletViewModel(get()) }
    viewModel { BuysViewModel(get()) }
    viewModel { BillsViewModel(get()) }
}

val dataModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().cardsDao() }
    single { get<AppDatabase>().expensesDao() }
    single { CardsRemoteDataSource(get()) }
    single { ExpensesRemoteDataSource(get()) }
    single<CardsRepository> { CardsDataRepository(get()) }
    single<ExpensesRepository> { ExpensesDataRepository(get()) }
}

@RequiresApi(Build.VERSION_CODES.P)
val storageModule = module {
    single { LocalStorage(get()) }
}

object MainModuleInitializer {
    fun initialize() = loadKoinModules(listOf(viewModelModule, dataModule, storageModule))
}