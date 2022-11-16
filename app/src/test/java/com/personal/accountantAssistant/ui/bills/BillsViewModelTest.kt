package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBillsProviders
import com.personal.accountantAssistant.providers.MockErrorProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class BillsViewModelTest : BaseTest() {

    private lateinit var viewModel: BillsViewModel
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val repository = mockk<BillsRepository>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = BillsViewModel(analytics = analytics, repository = repository)
    }

    @Test
    fun shouldLoadBills() {
        viewModel.run {
            coEvery { repository.getBills() } returns MockBillsProviders.mockedFlowBills()
            getBills()
            coVerify { repository.getBills() }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadBills() {
        viewModel.run {
            coEvery { repository.getBills() } returns MockErrorProvider.mockErrorFlow()
            getBills()
            coVerify { repository.getBills() }
            assertNotNull(errorMessage.value)
        }
    }
}