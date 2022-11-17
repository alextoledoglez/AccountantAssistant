package com.personal.accountantAssistant.di

import android.app.NotificationManager
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.remote.*
import com.personal.accountantAssistant.data.repository.*
import com.personal.accountantAssistant.domain.repository.*
import com.personal.accountantAssistant.domain.useCases.*
import com.personal.accountantAssistant.providers.*
import com.personal.accountantAssistant.services.NotificationService
import com.personal.accountantAssistant.services.SignInService
import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.login.LoginViewModel
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.Executors

object MainModule {

    private val viewModelModule = module {
        viewModel { LoginViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { WalletViewModel(get(), get(), get()) }
        viewModel { BuysViewModel(get(), get()) }
        viewModel { BillsViewModel(get(), get()) }
        viewModel { MenuViewModel(get(), get()) }
    }

    private val useCasesModule = module {
        single<SetFirstDateUseCase> { SetFirstDateUseCaseImpl(get()) }
        single<GetPeriodDatesUseCase> { GetPeriodDatesUseCaseImpl(get()) }
        single<SetLastDateUseCase> { SetLastDateUseCaseImpl(get()) }
        single<SetPeriodDatesUseCase> { SetPeriodDatesUseCaseImpl(get()) }
        single<SetAvailableMoneyUseCase> { SetAvailableMoneyUseCaseImpl(get()) }
        single<GetAvailableMoneyUseCase> { GetAvailableMoneyUseCaseImpl(get()) }
        single<GetExpensesUseCase> { GetExpensesUseCaseImpl(get()) }
        single<GetSignedUserUseCase> { GetSignedUserUseCaseImpl(get()) }
        single<SetSignedUserUseCase> { SetSignedUserUseCaseImpl(get()) }
        single<GetBillsDueSoonUseCase> { GetBillsDueSoonUseCaseImpl(get()) }
        single<GetBillsDueTodayUseCase> { GetBillsDueTodayUseCaseImpl(get()) }
        single<GetNotificationTokenUseCase> { GetNotificationTokenUseCaseImpl(get()) }
        single<GetLocalNotificationTokenUseCase> { GetLocalNotificationTokenUseCaseImpl(get()) }
        single<SetLocalNotificationTokenUseCase> { SetLocalNotificationTokenUseCaseImpl(get()) }
        single<SubscribeNotificationTopicUseCase> { SubscribeNotificationTopicUseCaseImpl(get()) }
    }

    private val dataModule = module {
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

    private val providersModule = module {
        single { AnalyticsProvider() }
        single { CrashlyticsProvider() }
        single { AdProvider(get(), get()) }
        single { NotificationProvider() }
        single { RemoteConfigProvider() }
    }

    private val utilsModule = module {
        single { GsonFactory() }
        single { LocalStorage(get()) }
        single { MobileAds.initialize(get()) {} }
        single { GoogleSignInOptions.DEFAULT_SIGN_IN }
        single { Executors.newSingleThreadExecutor() }
        single { AndroidHttp.newCompatibleTransport() }
        single { GoogleSignInOptions.Builder(get()) }
        single { GoogleAccountCredential.usingOAuth2(get(), get()) }
    }

    private val managersModule = module {
        single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    }

    private val servicesModule = module {
        single { NotificationService() }
        single { SignInService(get(), get(), get(), get()) }
    }

    fun getModules() = listOf(
        viewModelModule,
        useCasesModule,
        dataModule,
        providersModule,
        utilsModule,
        managersModule,
        servicesModule
    )
}