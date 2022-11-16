package com.personal.accountantAssistant.ui.wallet

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.domain.useCases.SetAvailableMoneyUseCase
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockCardsProviders
import com.personal.accountantAssistant.providers.MockErrorProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class WalletViewModelTest : BaseTest() {

    private lateinit var viewModel: WalletViewModel
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val setAvailableMoney = mockk<SetAvailableMoneyUseCase>(relaxed = true)
    private val repository = mockk<CardsRepository>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = WalletViewModel(
            analytics = analytics,
            setAvailableMoney = setAvailableMoney,
            repository = repository
        )
    }

    @Test
    fun shouldLoadCards() {
        viewModel.run {
            coEvery { repository.getCards() } returns MockCardsProviders.mockedFlowCards()
            loadCards()
            coVerify { repository.getCards() }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadCards() {
        viewModel.run {
            coEvery { repository.getCards() } returns MockErrorProvider.mockErrorFlow()
            loadCards()
            coVerify { repository.getCards() }
            assertNotNull(errorMessage.value)
        }
    }
}