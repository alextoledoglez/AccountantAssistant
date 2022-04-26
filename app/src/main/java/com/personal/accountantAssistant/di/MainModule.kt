package com.personal.accountantAssistant.di

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.DriveScopes
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.remote.BillsRemoteDataSource
import com.personal.accountantAssistant.data.remote.BuysRemoteDataSource
import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.data.remote.UserRemoteDataSource
import com.personal.accountantAssistant.data.repository.BillsDataRepository
import com.personal.accountantAssistant.data.repository.BuysDataRepository
import com.personal.accountantAssistant.data.repository.CardsDataRepository
import com.personal.accountantAssistant.data.repository.UserDataRepository
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.domain.repository.UserRepository
import com.personal.accountantAssistant.domain.useCases.*
import com.personal.accountantAssistant.providers.AdProvider
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider
import com.personal.accountantAssistant.services.DriveService
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.login.LoginViewModel
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.util.concurrent.Executors

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { WalletViewModel(get(), get(), get()) }
    viewModel { BuysViewModel(get(), get()) }
    viewModel { BillsViewModel(get(), get()) }
    viewModel { MenuViewModel(get(), get()) }
}

val useCasesModule = module {
    single<SetFirstDateUseCase> { SetFirstDateUseCaseImpl(get()) }
    single<GetFirstDateUseCase> { GetFirstDateUseCaseImpl(get()) }
    single<SetLastDateUseCase> { SetLastDateUseCaseImpl(get()) }
    single<GetLastDateUseCase> { GetLastDateUseCaseImpl(get()) }
    single<SetPeriodDatesUseCase> { SetPeriodDatesUseCaseImpl(get()) }
    single<SetAvailableMoneyUseCase> { SetAvailableMoneyUseCaseImpl(get()) }
    single<GetAvailableMoneyUseCase> { GetAvailableMoneyUseCaseImpl(get()) }
}

val dataModule = module {
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().cardsDao() }
    single { get<AppDatabase>().expensesDao() }
    single { CardsRemoteDataSource(get()) }
    single { BuysRemoteDataSource(get()) }
    single { BillsRemoteDataSource(get()) }
    single { UserRemoteDataSource(get()) }
    single<CardsRepository> { CardsDataRepository(get()) }
    single<BuysRepository> { BuysDataRepository(get()) }
    single<BillsRepository> { BillsDataRepository(get()) }
    single<UserRepository> { UserDataRepository(get()) }
}

val providersModule = module {
    single { AnalyticsProvider() }
    single { CrashlyticsProvider() }
    single { AdProvider(get(), get()) }
}

val utilsModule = module {
    single { GsonFactory() }
    single { LocalStorage(get()) }
    single { Scope(DriveScopes.DRIVE) }
    single { listOf(DriveScopes.DRIVE) }
    single { MobileAds.initialize(get()) {} }
    single { GoogleSignInOptions.DEFAULT_SIGN_IN }
    single { Executors.newSingleThreadExecutor() }
    single { AndroidHttp.newCompatibleTransport() }
    single { GoogleSignInOptions.Builder(get()).requestScopes(get()) }
    single { GoogleAccountCredential.usingOAuth2(get(), get()) }
}

val servicesModule = module {
    single { SignInService(get(), get(), get(), get()) }
    single { DriveService(get(), get(), get(), get(), get(), get(), get()) }
}

object MainModuleInitializer {
    fun initialize() = loadKoinModules(
        listOf(
            viewModelModule,
            useCasesModule,
            dataModule,
            providersModule,
            utilsModule,
            servicesModule
        )
    )
}