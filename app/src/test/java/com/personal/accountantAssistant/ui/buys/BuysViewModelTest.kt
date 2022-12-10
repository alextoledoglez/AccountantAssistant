package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.useCases.buys.*
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBuysProviders.mockedAllBuysActiveFlow
import com.personal.accountantAssistant.providers.MockBuysProviders.mockedAllBuysInactiveFlow
import com.personal.accountantAssistant.providers.MockBuysProviders.mockedBuy
import com.personal.accountantAssistant.providers.MockBuysProviders.mockedBuysFlow
import com.personal.accountantAssistant.providers.MockBuysProviders.mockedBuysSummaryFlow
import com.personal.accountantAssistant.providers.MockErrorProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class BuysViewModelTest : BaseTest() {

    private lateinit var viewModel: BuysViewModel
    private val getBuysUseCase = mockk<GetBuysUseCase>(relaxed = true)
    private val getBuysSummaryUseCase = mockk<GetBuysSummaryUseCase>(relaxed = true)
    private val saveBuyUseCase = mockk<SaveBuyUseCase>(relaxed = true)
    private val activeBuysUseCase = mockk<ActiveBuysUseCase>(relaxed = true)
    private val deleteBuysUseCase = mockk<DeleteBuysUseCase>(relaxed = true)
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = BuysViewModel(
            getBuysUseCase = getBuysUseCase,
            getBuysSummaryUseCase = getBuysSummaryUseCase,
            saveBuyUseCase = saveBuyUseCase,
            activeBuysUseCase = activeBuysUseCase,
            deleteBuysUseCase = deleteBuysUseCase,
            analytics = analytics
        )
    }

    @Test
    fun shouldLoadBuys() {
        viewModel.run {
            coEvery { getBuysUseCase() } returns mockedBuysFlow()
            loadBuys()
            coVerify { getBuysUseCase() }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadBuys() {
        viewModel.run {
            coEvery { getBuysUseCase() } returns MockErrorProvider.mockErrorFlow()
            loadBuys()
            coVerify { getBuysUseCase() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldLoadSummary() {
        viewModel.run {
            coEvery { getBuysSummaryUseCase() } returns mockedBuysSummaryFlow()
            loadSummary()
            coVerify { getBuysSummaryUseCase() }
            assertNotNull(summary.value)
        }
    }

    @Test
    fun shouldNotLoadSummary() {
        viewModel.run {
            coEvery { getBuysSummaryUseCase() } returns MockErrorProvider.mockErrorFlow()
            loadSummary()
            coVerify { getBuysSummaryUseCase() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSaveBuy() {
        viewModel.run {
            coEvery { saveBuyUseCase(any()) } returns mockedBuysFlow()
            saveBuy(mockedBuy())
            coVerify { saveBuyUseCase(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveBuy() {
        viewModel.run {
            coEvery { saveBuyUseCase(any()) } returns MockErrorProvider.mockErrorFlow()
            saveBuy(mockedBuy())
            coVerify { saveBuyUseCase(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllBuysActive() {
        viewModel.run {
            coEvery { activeBuysUseCase.setAllBuysActive(any()) } returns mockedAllBuysActiveFlow()
            setAllBuysActive(isActive = true)
            coVerify { activeBuysUseCase.setAllBuysActive(any()) }
            assertTrue(buys.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBuysInactive() {
        viewModel.run {
            coEvery { activeBuysUseCase.setAllBuysActive(any()) } returns mockedAllBuysInactiveFlow()
            setAllBuysActive(isActive = false)
            coVerify { activeBuysUseCase.setAllBuysActive(any()) }
            assertTrue(buys.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBuysActive() {
        viewModel.run {
            coEvery { activeBuysUseCase.setAllBuysActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllBuysActive(isActive = false)
            coVerify { activeBuysUseCase.setAllBuysActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveBuy() {
        viewModel.run {
            coEvery { activeBuysUseCase.switchActiveBuy(any()) } returns mockedBuysFlow()
            switchActiveBuy(mockedBuy())
            coVerify { activeBuysUseCase.switchActiveBuy(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveBuy() {
        viewModel.run {
            coEvery { activeBuysUseCase.switchActiveBuy(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveBuy(mockedBuy())
            coVerify { activeBuysUseCase.switchActiveBuy(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteBuy() {
        viewModel.run {
            coEvery { deleteBuysUseCase.deleteBuy(any()) } returns mockedBuysFlow()
            deleteBuy(mockedBuy())
            coVerify { deleteBuysUseCase.deleteBuy(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteBuy() {
        viewModel.run {
            coEvery { deleteBuysUseCase.deleteBuy(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteBuy(mockedBuy())
            coVerify { deleteBuysUseCase.deleteBuy(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllBuys() {
        viewModel.run {
            coEvery { deleteBuysUseCase.deleteAllBuys() } returns mockedBuysFlow()
            deleteAllBuys()
            coVerify { deleteBuysUseCase.deleteAllBuys() }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllBuys() {
        viewModel.run {
            coEvery { deleteBuysUseCase.deleteAllBuys() } returns MockErrorProvider.mockErrorFlow()
            deleteAllBuys()
            coVerify { deleteBuysUseCase.deleteAllBuys() }
            assertNotNull(errorMessage.value)
        }
    }
}