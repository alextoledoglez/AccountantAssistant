package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBuysProviders
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
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val repository = mockk<BuysRepository>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = BuysViewModel(analytics = analytics, repository = repository)
    }

    @Test
    fun shouldLoadBuys() {
        viewModel.run {
            coEvery { repository.getBuys() } returns MockBuysProviders.mockedBuysFlow()
            getBuys()
            coVerify { repository.getBuys() }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadBuys() {
        viewModel.run {
            coEvery { repository.getBuys() } returns MockErrorProvider.mockErrorFlow()
            getBuys()
            coVerify { repository.getBuys() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldLoadSummary() {
        viewModel.run {
            coEvery { repository.getSummary() } returns mockedBuysSummaryFlow()
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
    fun shouldSaveBuy() {
        viewModel.run {
            coEvery { repository.saveBuy(any()) } returns mockedBuysFlow()
            saveBuy(mockedBuy())
            coVerify { repository.saveBuy(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveBuy() {
        viewModel.run {
            coEvery { repository.saveBuy(any()) } returns MockErrorProvider.mockErrorFlow()
            saveBuy(mockedBuy())
            coVerify { repository.saveBuy(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllBuysActive() {
        viewModel.run {
            coEvery { repository.setAllBuysActive(any()) } returns mockedAllBuysActiveFlow()
            setAllBuysActive(isActive = true)
            coVerify { repository.setAllBuysActive(any()) }
            assertTrue(buys.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBuysInactive() {
        viewModel.run {
            coEvery { repository.setAllBuysActive(any()) } returns mockedAllBuysInactiveFlow()
            setAllBuysActive(isActive = false)
            coVerify { repository.setAllBuysActive(any()) }
            assertTrue(buys.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllBuysActive() {
        viewModel.run {
            coEvery { repository.setAllBuysActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllBuysActive(isActive = false)
            coVerify { repository.setAllBuysActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveBuy() {
        viewModel.run {
            coEvery { repository.switchActiveBuy(any()) } returns mockedBuysFlow()
            switchActiveBuy(mockedBuy())
            coVerify { repository.switchActiveBuy(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveBuy() {
        viewModel.run {
            coEvery { repository.switchActiveBuy(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveBuy(mockedBuy())
            coVerify { repository.switchActiveBuy(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteBuy() {
        viewModel.run {
            coEvery { repository.deleteBuy(any()) } returns mockedBuysFlow()
            deleteBuy(mockedBuy())
            coVerify { repository.deleteBuy(any()) }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteBuy() {
        viewModel.run {
            coEvery { repository.deleteBuy(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteBuy(mockedBuy())
            coVerify { repository.deleteBuy(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllBuys() {
        viewModel.run {
            coEvery { repository.deleteAllBuys() } returns mockedBuysFlow()
            deleteAllBuys()
            coVerify { repository.deleteAllBuys() }
            assertTrue(buys.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllBuys() {
        viewModel.run {
            coEvery { repository.deleteAllBuys() } returns MockErrorProvider.mockErrorFlow()
            deleteAllBuys()
            coVerify { repository.deleteAllBuys() }
            assertNotNull(errorMessage.value)
        }
    }
}