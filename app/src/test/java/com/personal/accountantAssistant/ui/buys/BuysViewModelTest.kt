package com.personal.accountantAssistant.ui.buys

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.BuysRepository
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockBuysProviders
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
            coEvery { repository.getBuys() } returns MockBuysProviders.mockedFlowBuys()
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
}