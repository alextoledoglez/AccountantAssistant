package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.remote.BillsRemoteDataSource
import com.personal.accountantAssistant.data.remote.BuysRemoteDataSource
import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.data.repository.BillsDataRepository
import com.personal.accountantAssistant.data.repository.BuysDataRepository
import com.personal.accountantAssistant.data.repository.CardsDataRepository
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { WalletViewModel(get()) }
    viewModel { BuysViewModel(get()) }
    viewModel { BillsViewModel(get()) }
}

val dataModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().cardsDao() }
    single { get<AppDatabase>().expensesDao() }
    single { CardsRemoteDataSource(get()) }
    single { BuysRemoteDataSource(get()) }
    single { BillsRemoteDataSource(get()) }
    single<CardsRepository> { CardsDataRepository(get()) }
    single<BuysRepository> { BuysDataRepository(get()) }
    single<BillsRepository> { BillsDataRepository(get()) }
}

val storageModule = module {
    single { LocalStorage(get()) }
}

object MainModuleInitializer {
    fun initialize() = loadKoinModules(listOf(viewModelModule, dataModule, storageModule))
}