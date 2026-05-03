package com.personal.accountantAssistant.ui.bills

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.useCases.bills.*
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedAllBillsActiveFlow
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedAllBillsInactiveFlow
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedBill
import com.personal.accountantAssistant.providers.MockBillsProviders.mockedBillsFlow
import com.personal.accountantAssistant.providers.MockErrorProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class BillsViewModelTest : BaseTest() {

    private lateinit var viewModel: BillsViewModel
    private val getBillsUseCase = mockk<GetBillsUseCase>(relaxed = true)
    private val saveBillUseCase = mockk<SaveBillUseCase>(relaxed = true)
    private val activeBillsUseCase = mockk<ActiveBillsUseCase>(relaxed = true)
    private val deleteBillsUseCase = mockk<DeleteBillsUseCase>(relaxed = true)
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = BillsViewModel(
            getBillsUseCase = getBillsUseCase,
            saveBillUseCase = saveBillUseCase,
            activeBillsUseCase = activeBillsUseCase,
            deleteBillsUseCase = deleteBillsUseCase,
            analytics = analytics
        )
    }

    @Test
    fun shouldLoadBills() {
        viewModel.run {
            coEvery { getBillsUseCase() } returns mockedBillsFlow()
            loadBills()
            coVerify { getBillsUseCase() }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadBills() {
        viewModel.run {
            coEvery { getBillsUseCase() } returns MockErrorProvider.mockErrorFlow()
            loadBills()
            coVerify { getBillsUseCase() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSummaryReflectActiveBills() {
        viewModel.run {
            coEvery { getBillsUseCase() } returns mockedAllBillsActiveFlow()
            loadBills()
            coVerify { getBillsUseCase() }
            assertTrue(summary.value?.activeCount.orZero() > 0)
        }
    }

    @Test
    fun shouldSaveBill() {
        viewModel.run {
            coEvery { saveBillUseCase(any()) } returns mockedBillsFlow()
            saveBill(mockedBill())
            coVerify { saveBillUseCase(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveBill() {
        viewModel.run {
            coEvery { saveBillUseCase(any()) } returns MockErrorProvider.mockErrorFlow()
            saveBill(mockedBill())
            coVerify { saveBillUseCase(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllBillsActive() {
        viewModel.run {
            coEvery { activeBillsUseCase.setAllBillsActive(any()) } returns mockedAllBillsActiveFlow()
            setAllBillsActive(isActive = true)
            coVerify { activeBillsUseCase.setAllBillsActive(any()) }
            assertTrue(bills.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBillsInactive() {
        viewModel.run {
            coEvery { activeBillsUseCase.setAllBillsActive(any()) } returns mockedAllBillsInactiveFlow()
            setAllBillsActive(isActive = false)
            coVerify { activeBillsUseCase.setAllBillsActive(any()) }
            assertTrue(bills.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBillsActive() {
        viewModel.run {
            coEvery { activeBillsUseCase.setAllBillsActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllBillsActive(isActive = false)
            coVerify { activeBillsUseCase.setAllBillsActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveBill() {
        viewModel.run {
            coEvery { activeBillsUseCase.switchActiveBill(any()) } returns mockedBillsFlow()
            switchActiveBill(mockedBill())
            coVerify { activeBillsUseCase.switchActiveBill(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveBill() {
        viewModel.run {
            coEvery { activeBillsUseCase.switchActiveBill(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveBill(mockedBill())
            coVerify { activeBillsUseCase.switchActiveBill(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteBill() {
        viewModel.run {
            coEvery { deleteBillsUseCase.deleteBill(any()) } returns mockedBillsFlow()
            deleteBill(mockedBill())
            coVerify { deleteBillsUseCase.deleteBill(any()) }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteBill() {
        viewModel.run {
            coEvery { deleteBillsUseCase.deleteBill(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteBill(mockedBill())
            coVerify { deleteBillsUseCase.deleteBill(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllBills() {
        viewModel.run {
            coEvery { deleteBillsUseCase.deleteAllBills() } returns mockedBillsFlow()
            deleteAllBills()
            coVerify { deleteBillsUseCase.deleteAllBills() }
            assertTrue(bills.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllBills() {
        viewModel.run {
            coEvery { deleteBillsUseCase.deleteAllBills() } returns MockErrorProvider.mockErrorFlow()
            deleteAllBills()
            coVerify { deleteBillsUseCase.deleteAllBills() }
            assertNotNull(errorMessage.value)
        }
    }
}