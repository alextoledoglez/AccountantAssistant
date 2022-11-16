package com.personal.accountantAssistant.ui.home

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.domain.useCases.GetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.GetFirstDateUseCase
import com.personal.accountantAssistant.domain.useCases.GetLastDateUseCase
import com.personal.accountantAssistant.domain.useCases.SetPeriodDatesUseCase
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockErrorProvider
import com.personal.accountantAssistant.providers.MockHomeProviders
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Ignore
import org.junit.Test

class HomeViewModelTest : BaseTest() {

    private lateinit var viewModel: HomeViewModel
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val getFirstDate = mockk<GetFirstDateUseCase>(relaxed = true)
    private val getLastDate = mockk<GetLastDateUseCase>(relaxed = true)
    private val setPeriodDates = mockk<SetPeriodDatesUseCase>(relaxed = true)
    private val getAvailableMoney = mockk<GetAvailableMoneyUseCase>(relaxed = true)
    private val buysRepository = mockk<BuysRepository>(relaxed = true)
    private val billsRepository = mockk<BillsRepository>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = HomeViewModel(
            analytics = analytics,
            getFirstDate = getFirstDate,
            getLastDate = getLastDate,
            setPeriodDates = setPeriodDates,
            getAvailableMoney = getAvailableMoney,
            buysRepository = buysRepository,
            billsRepository = billsRepository
        )
    }

    @Ignore
    fun shouldLoadPeriodDates() {
        viewModel.run {
            coEvery { Any() } returns MockHomeProviders.mockedPeriodDates()
            loadPeriodDates()
            assertNotNull(periodDates.value)
        }
    }

    @Ignore
    fun shouldNotLoadPeriodDates() {
        viewModel.run {
            coEvery { Any() } returns MockErrorProvider.mockErrorFlow()
            loadPeriodDates()
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldLoadAvailableMoney() {
        viewModel.run {
            coEvery { getAvailableMoney() } returns MockHomeProviders.mockedAvailableMoney()
            loadAvailableMoney()
            coVerify { getAvailableMoney() }
            assertNotNull(availableMoney.value)
        }
    }

    @Test
    fun shouldNotLoadAvailableMoney() {
        viewModel.run {
            coEvery { getAvailableMoney() } returns MockErrorProvider.mockErrorFlow()
            loadAvailableMoney()
            coVerify { getAvailableMoney() }
            assertNotNull(errorMessage.value)
        }
    }

    @Ignore
    fun shouldLoadExpenses() {
        viewModel.run {
            coEvery { Any() } returns MockHomeProviders.mockedFlowExpensesValues()
            loadExpenses(MockHomeProviders.mockedLastDate())
            assertNotNull(expensesValues.value)
        }
    }

    @Ignore
    fun shouldNotLoadExpenses() {
        viewModel.run {
            coEvery { Any() } returns MockErrorProvider.mockErrorFlow()
            loadExpenses(MockHomeProviders.mockedLastDate())
            assertNotNull(errorMessage.value)
        }
    }
}