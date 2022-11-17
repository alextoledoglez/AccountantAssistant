package com.personal.accountantAssistant.ui.home

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.useCases.GetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.GetExpensesUseCase
import com.personal.accountantAssistant.domain.useCases.GetPeriodDatesUseCase
import com.personal.accountantAssistant.domain.useCases.SetPeriodDatesUseCase
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockErrorProvider
import com.personal.accountantAssistant.providers.MockHomeProviders
import com.personal.accountantAssistant.providers.MockHomeProviders.mockedFlowAvailableMoney
import com.personal.accountantAssistant.providers.MockHomeProviders.mockedFlowExpensesValues
import com.personal.accountantAssistant.providers.MockHomeProviders.mockedPeriodDates
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class HomeViewModelTest : BaseTest() {

    private lateinit var viewModel: HomeViewModel
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val getPeriodDates = mockk<GetPeriodDatesUseCase>(relaxed = true)
    private val setPeriodDates = mockk<SetPeriodDatesUseCase>(relaxed = true)
    private val getAvailableMoney = mockk<GetAvailableMoneyUseCase>(relaxed = true)
    private val getExpenses = mockk<GetExpensesUseCase>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = HomeViewModel(
            analytics = analytics,
            getPeriodDates = getPeriodDates,
            setPeriodDates = setPeriodDates,
            getAvailableMoney = getAvailableMoney,
            getExpenses = getExpenses
        )
    }

    @Test
    fun shouldLoadAvailableMoney() {
        viewModel.run {
            coEvery { getAvailableMoney() } returns mockedFlowAvailableMoney()
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

    @Test
    fun shouldLoadPeriodDates() {
        viewModel.run {
            coEvery { getPeriodDates() } returns mockedPeriodDates()
            loadPeriodDates()
            coVerify { getPeriodDates() }
            assertNotNull(periodDates.value)
        }
    }

    @Test
    fun shouldNotLoadPeriodDates() {
        viewModel.run {
            coEvery { getPeriodDates() } returns MockErrorProvider.mockErrorFlow()
            loadPeriodDates()
            coVerify { getPeriodDates() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldLoadExpenses() {
        viewModel.run {
            val lastDate = MockHomeProviders.mockedLastDate()
            val available = MockHomeProviders.mockedAvailableMoney()
            coEvery { getExpenses(lastDate, available) } returns mockedFlowExpensesValues()
            loadExpenses(lastDate, available)
            coVerify { getExpenses(lastDate, available) }
            assertNotNull(expensesValues.value)
        }
    }

    @Test
    fun shouldNotLoadExpenses() {
        viewModel.run {
            val lastDate = MockHomeProviders.mockedLastDate()
            val available = MockHomeProviders.mockedAvailableMoney()
            coEvery { getExpenses(lastDate, available) } returns MockErrorProvider.mockErrorFlow()
            loadExpenses(lastDate, available)
            coVerify { getExpenses(lastDate, available) }
            assertNotNull(errorMessage.value)
        }
    }
}