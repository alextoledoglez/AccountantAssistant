package com.personal.accountantAssistant.di

import com.personal.accountantAssistant.ui.bills.BillsViewModel
import com.personal.accountantAssistant.ui.buys.BuysViewModel
import com.personal.accountantAssistant.ui.home.HomeViewModel
import com.personal.accountantAssistant.ui.login.LoginViewModel
import com.personal.accountantAssistant.ui.menu.MenuViewModel
import com.personal.accountantAssistant.ui.wallet.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object ViewModelsModule {

    fun getViewModels() = module {

        viewModel {
            LoginViewModel(
                getSignedUser = get(),
                setSignedUser = get(),
                getNotificationToken = get(),
                setLocalNotificationToken = get(),
                subscribeNotificationTopic = get(),
                analytics = get()
            )
        }
        viewModel {
            HomeViewModel(
                getPeriodDates = get(),
                setPeriodDates = get(),
                getAvailableMoney = get(),
                getExpenses = get(),
                analytics = get()
            )
        }
        viewModel {
            WalletViewModel(
                getCardsUseCase = get(),
                getCardsSummaryUseCase = get(),
                setAvailableMoneyUseCase = get(),
                saveCardUseCase = get(),
                activeCardsUseCase = get(),
                deleteCardsUseCase = get(),
                analytics = get()
            )
        }
        viewModel {
            BuysViewModel(
                getBuysUseCase = get(),
                getBuysSummaryUseCase = get(),
                saveBuyUseCase = get(),
                activeBuysUseCase = get(),
                deleteBuysUseCase = get(),
                analytics = get()
            )
        }
        viewModel {
            BillsViewModel(
                getBillsUseCase = get(),
                getBillsSummaryUseCase = get(),
                saveBillUseCase = get(),
                activeBillsUseCase = get(),
                deleteBillsUseCase = get(),
                analytics = get()
            )
        }
        viewModel {
            MenuViewModel(
                getSignedUser = get(),
                setSignedUser = get(),
                analytics = get()
            )
        }
    }
}