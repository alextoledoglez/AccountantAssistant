package com.personal.accountantAssistant.ui.wallet

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.domain.useCases.SetAvailableMoneyUseCase
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAllCardsActiveFlow
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAllCardsInactiveFlow
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAvailableValue
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedCard
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedCardSummaryFlow
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedCardsFlow
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
            coEvery { repository.getCards() } returns mockedCardsFlow()
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

    @Test
    fun shouldLoadSummary() {
        viewModel.run {
            coEvery { repository.getSummary() } returns mockedCardSummaryFlow()
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
    fun shouldSetAvailableMoney() {
        viewModel.run {
            coEvery { setAvailableMoney(mockedAvailableValue()) } returns Unit
            setAvailableMoney(mockedAvailableValue())
            assertNull(errorMessage.value)
            assertTrue(flipper.value == FlipperViews.DATA)
        }
    }

    @Test
    fun shouldSaveCard() {
        viewModel.run {
            coEvery { repository.saveCard(any()) } returns mockedCardsFlow()
            saveCard(mockedCard())
            coVerify { repository.saveCard(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveCard() {
        viewModel.run {
            coEvery { repository.saveCard(any()) } returns MockErrorProvider.mockErrorFlow()
            saveCard(mockedCard())
            coVerify { repository.saveCard(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllCardsActive() {
        viewModel.run {
            coEvery { repository.setAllCardsActive(any()) } returns mockedAllCardsActiveFlow()
            setAllCardsActive(isActive = true)
            coVerify { repository.setAllCardsActive(any()) }
            assertTrue(cards.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllCardsInactive() {
        viewModel.run {
            coEvery { repository.setAllCardsActive(any()) } returns mockedAllCardsInactiveFlow()
            setAllCardsActive(isActive = false)
            coVerify { repository.setAllCardsActive(any()) }
            assertTrue(cards.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllCardsActive() {
        viewModel.run {
            coEvery { repository.setAllCardsActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllCardsActive(isActive = false)
            coVerify { repository.setAllCardsActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveCard() {
        viewModel.run {
            coEvery { repository.switchActiveCard(any()) } returns mockedCardsFlow()
            switchActiveCard(mockedCard())
            coVerify { repository.switchActiveCard(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveCard() {
        viewModel.run {
            coEvery { repository.switchActiveCard(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveCard(mockedCard())
            coVerify { repository.switchActiveCard(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteCard() {
        viewModel.run {
            coEvery { repository.deleteCard(any()) } returns mockedCardsFlow()
            deleteCard(mockedCard())
            coVerify { repository.deleteCard(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteCard() {
        viewModel.run {
            coEvery { repository.deleteCard(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteCard(mockedCard())
            coVerify { repository.deleteCard(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllCards() {
        viewModel.run {
            coEvery { repository.deleteAllCards() } returns mockedCardsFlow()
            deleteAllCards()
            coVerify { repository.deleteAllCards() }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllCards() {
        viewModel.run {
            coEvery { repository.deleteAllCards() } returns MockErrorProvider.mockErrorFlow()
            deleteAllCards()
            coVerify { repository.deleteAllCards() }
            assertNotNull(errorMessage.value)
        }
    }
}