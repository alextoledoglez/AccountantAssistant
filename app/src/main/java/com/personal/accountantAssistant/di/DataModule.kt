package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.remote.*
import com.personal.accountantAssistant.data.repository.*
import com.personal.accountantAssistant.domain.repository.*
import com.personal.accountantAssistant.domain.useCases.*
import com.personal.accountantAssistant.domain.useCases.bills.*
import com.personal.accountantAssistant.domain.useCases.buys.*
import com.personal.accountantAssistant.domain.useCases.home.*
import com.personal.accountantAssistant.domain.useCases.login.*
import com.personal.accountantAssistant.domain.useCases.wallet.*
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.login.LoginViewModel
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

object DataModule {

    fun getData() = module {
        single { AppDatabase.getInstance(get()) }
        single { get<AppDatabase>().cardsDao() }
        single { get<AppDatabase>().expensesDao() }
        single { CardsRemoteDataSource(get()) }
        single { BuysRemoteDataSource(get()) }
        single { BillsRemoteDataSource(get()) }
        single { ExpensesRemoteDataSource(get()) }
        single { UserRemoteDataSource(get()) }
        single<CardsRepository> { CardsDataRepository(get()) }
        single<BuysRepository> { BuysDataRepository(get()) }
        single<BillsRepository> { BillsDataRepository(get()) }
        single<ExpensesRepository> { ExpensesDataRepository(get()) }
        single<UserRepository> { UserDataRepository(get()) }
        single<NotificationRepository> { NotificationDataRepository(get(), get()) }
    }
}