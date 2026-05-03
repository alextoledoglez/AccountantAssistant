package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.remote.BarcodeProductRemoteDataSource
import com.personal.accountantAssistant.data.remote.BillsRemoteDataSource
import com.personal.accountantAssistant.data.remote.BuysRemoteDataSource
import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.google.firebase.database.FirebaseDatabase
import com.personal.accountantAssistant.data.remote.CompanyRemoteDataSource
import com.personal.accountantAssistant.data.remote.ExpensesRemoteDataSource
import com.personal.accountantAssistant.data.remote.UserRemoteDataSource
import com.personal.accountantAssistant.data.repository.BarcodeProductDataRepository
import com.personal.accountantAssistant.data.repository.BillsDataRepository
import com.personal.accountantAssistant.data.repository.BuysDataRepository
import com.personal.accountantAssistant.data.repository.CardsDataRepository
import com.personal.accountantAssistant.data.repository.CompanyDataRepository
import com.personal.accountantAssistant.data.repository.ExpensesDataRepository
import com.personal.accountantAssistant.data.repository.NotificationDataRepository
import com.personal.accountantAssistant.data.repository.UserDataRepository
import com.personal.accountantAssistant.domain.repository.BarcodeProductRepository
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.domain.repository.CompanyRepository
import com.personal.accountantAssistant.domain.repository.ExpensesRepository
import com.personal.accountantAssistant.domain.repository.NotificationRepository
import com.personal.accountantAssistant.domain.repository.UserRepository
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
        single { FirebaseDatabase.getInstance() }
        single { CompanyRemoteDataSource(get()) }
        single { BarcodeProductRemoteDataSource() }
        single<CardsRepository> { CardsDataRepository(get()) }
        single<BuysRepository> { BuysDataRepository(get()) }
        single<BillsRepository> { BillsDataRepository(get()) }
        single<ExpensesRepository> { ExpensesDataRepository(get()) }
        single<UserRepository> { UserDataRepository(get()) }
        single<CompanyRepository> { CompanyDataRepository(get()) }
        single<BarcodeProductRepository> { BarcodeProductDataRepository(get()) }
        single<NotificationRepository> { NotificationDataRepository(get(), get()) }
    }
}