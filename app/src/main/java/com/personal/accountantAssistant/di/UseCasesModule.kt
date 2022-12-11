package com.personal.accountantAssistant.di

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

object UseCasesModule {

    fun getUseCases() = module {
        single<GetCardsUseCase> { GetCardsUseCaseImpl(get()) }
        single<GetCardsSummaryUseCase> { GetCardsSummaryUseCaseImpl(get()) }
        single<SaveCardUseCase> { SaveCardUseCaseImpl(get()) }
        single<ActiveCardsUseCase> { ActiveCardsUseCaseImpl(get()) }
        single<DeleteCardsUseCase> { DeleteCardsUseCaseImpl(get()) }
        single<GetBuysUseCase> { GetBuysUseCaseImpl(get()) }
        single<GetBuysSummaryUseCase> { GetBuysSummaryUseCaseImpl(get()) }
        single<SaveBuyUseCase> { SaveBuyUseCaseImpl(get()) }
        single<ActiveBuysUseCase> { ActiveBuysUseCaseImpl(get()) }
        single<DeleteBuysUseCase> { DeleteBuysUseCaseImpl(get()) }
        single<GetBillsUseCase> { GetBillsUseCaseImpl(get()) }
        single<GetBillsSummaryUseCase> { GetBillsSummaryUseCaseImpl(get()) }
        single<SaveBillUseCase> { SaveBillUseCaseImpl(get()) }
        single<ActiveBillsUseCase> { ActiveBillsUseCaseImpl(get()) }
        single<DeleteBillsUseCase> { DeleteBillsUseCaseImpl(get()) }
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
}