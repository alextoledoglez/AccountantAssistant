package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.BillsRepository
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedAllBillsActiveFlow
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedAllBillsInactiveFlow
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedBill
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedBillsFlow
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedBillsSummaryFlow
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
            coEvery { repository.getBills() } returns mockedBillsFlow()
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

    @Test
    fun shouldLoadSummary() {
        viewModel.run {
            coEvery { repository.getSummary() } returns mockedBillsSummaryFlow()
            loadSummary()
            coVerify { repository.getSummary() }
            assertNotNull(summary.value)
        }
    }

    @Test
    fun shouldNotLoadSummary() {
        viewModel.run {
            coEvery { repository.getSummary() } returns MockErrorProvider.mockErrorFlow()
            loadSummary()
            coVerify { repository.getSummary() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSaveBill() {
        viewModel.run {
            coEvery { repository.saveBill(any()) } returns mockedBillsFlow()
            saveBill(mockedBill())
            coVerify { repository.saveBill(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveBill() {
        viewModel.run {
            coEvery { repository.saveBill(any()) } returns MockErrorProvider.mockErrorFlow()
            saveBill(mockedBill())
            coVerify { repository.saveBill(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllBillsActive() {
        viewModel.run {
            coEvery { repository.setAllBillsActive(any()) } returns mockedAllBillsActiveFlow()
            setAllBillsActive(isActive = true)
            coVerify { repository.setAllBillsActive(any()) }
            assertTrue(bills.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBillsInactive() {
        viewModel.run {
            coEvery { repository.setAllBillsActive(any()) } returns mockedAllBillsInactiveFlow()
            setAllBillsActive(isActive = false)
            coVerify { repository.setAllBillsActive(any()) }
            assertTrue(bills.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBillsActive() {
        viewModel.run {
            coEvery { repository.setAllBillsActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllBillsActive(isActive = false)
            coVerify { repository.setAllBillsActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveBill() {
        viewModel.run {
            coEvery { repository.switchActiveBill(any()) } returns mockedBillsFlow()
            switchActiveBill(mockedBill())
            coVerify { repository.switchActiveBill(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveBill() {
        viewModel.run {
            coEvery { repository.switchActiveBill(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveBill(mockedBill())
            coVerify { repository.switchActiveBill(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteBill() {
        viewModel.run {
            coEvery { repository.deleteBill(any()) } returns mockedBillsFlow()
            deleteBill(mockedBill())
            coVerify { repository.deleteBill(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteBill() {
        viewModel.run {
            coEvery { repository.deleteBill(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteBill(mockedBill())
            coVerify { repository.deleteBill(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllBills() {
        viewModel.run {
            coEvery { repository.deleteAllBills() } returns mockedBillsFlow()
            deleteAllBills()
            coVerify { repository.deleteAllBills() }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllBills() {
        viewModel.run {
            coEvery { repository.deleteAllBills() } returns MockErrorProvider.mockErrorFlow()
            deleteAllBills()
            coVerify { repository.deleteAllBills() }
            assertNotNull(errorMessage.value)
        }
    }
}