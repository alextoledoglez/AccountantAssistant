package com.personal.accountantAssistant.ui.wallet

import com.personal.accountantAssistant.base.BaseTest
import com.personal.accountantAssistant.data.enums.FlipperViews
import com.personal.accountantAssistant.domain.useCases.SetAvailableMoneyUseCase
import com.personal.accountantAssistant.domain.useCases.wallet.*
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAllCardsActiveFlow
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAllCardsInactiveFlow
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedAvailableValue
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedCard
import com.personal.accountantAssistant.providers.MockCardsProviders.mockedCardsFlow
import com.personal.accountantAssistant.providers.MockErrorProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class WalletViewModelTest : BaseTest() {

    private lateinit var viewModel: WalletViewModel
    private val getCardsUseCase = mockk<GetCardsUseCase>(relaxed = true)
    private val setAvailableMoneyUseCase = mockk<SetAvailableMoneyUseCase>(relaxed = true)
    private val saveCardUseCase = mockk<SaveCardUseCase>(relaxed = true)
    private val activeCardsUseCase = mockk<ActiveCardsUseCase>(relaxed = true)
    private val deleteCardsUseCase = mockk<DeleteCardsUseCase>(relaxed = true)
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)

    override fun setup() {
        super.setup()
        viewModel = WalletViewModel(
            getCardsUseCase = getCardsUseCase,
            setAvailableMoneyUseCase = setAvailableMoneyUseCase,
            saveCardUseCase = saveCardUseCase,
            activeCardsUseCase = activeCardsUseCase,
            deleteCardsUseCase = deleteCardsUseCase,
            analytics = analytics
        )
    }

    @Test
    fun shouldLoadCards() {
        viewModel.run {
            coEvery { getCardsUseCase() } returns mockedCardsFlow()
            loadCards()
            coVerify { getCardsUseCase() }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotLoadCards() {
        viewModel.run {
            coEvery { getCardsUseCase() } returns MockErrorProvider.mockErrorFlow()
            loadCards()
            coVerify { getCardsUseCase() }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSummaryReflectActiveCards() {
        viewModel.run {
            coEvery { getCardsUseCase() } returns mockedAllCardsActiveFlow()
            loadCards()
            coVerify { getCardsUseCase() }
            assertTrue(summary.value?.activeCount.orZero() > 0)
        }
    }

    @Test
    fun shouldSetAvailableMoney() {
        viewModel.run {
            coEvery { setAvailableMoneyUseCase(any()) } returns flowEmit { }
            setAvailableMoney(mockedAvailableValue())
            assertNull(errorMessage.value)
            assertTrue(flipper.value == FlipperViews.DATA)
        }
    }

    @Test
    fun shouldSaveCard() {
        viewModel.run {
            coEvery { saveCardUseCase(any()) } returns mockedCardsFlow()
            saveCard(mockedCard())
            coVerify { saveCardUseCase(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSaveCard() {
        viewModel.run {
            coEvery { saveCardUseCase(any()) } returns MockErrorProvider.mockErrorFlow()
            saveCard(mockedCard())
            coVerify { saveCardUseCase(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSetAllCardsActive() {
        viewModel.run {
            coEvery { activeCardsUseCase.setAllCardsActive(any()) } returns mockedAllCardsActiveFlow()
            setAllCardsActive(isActive = true)
            coVerify { activeCardsUseCase.setAllCardsActive(any()) }
            assertTrue(cards.value?.any { it.isActive }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllCardsInactive() {
        viewModel.run {
            coEvery { activeCardsUseCase.setAllCardsActive(any()) } returns mockedAllCardsInactiveFlow()
            setAllCardsActive(isActive = false)
            coVerify { activeCardsUseCase.setAllCardsActive(any()) }
            assertTrue(cards.value?.any { it.isActive.not() }.orFalse())
        }
    }

    @Test
    fun shouldNotSetAllCardsActive() {
        viewModel.run {
            coEvery { activeCardsUseCase.setAllCardsActive(any()) } returns MockErrorProvider.mockErrorFlow()
            setAllCardsActive(isActive = false)
            coVerify { activeCardsUseCase.setAllCardsActive(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldSwitchActiveCard() {
        viewModel.run {
            coEvery { activeCardsUseCase.switchActiveCard(any()) } returns mockedCardsFlow()
            switchActiveCard(mockedCard())
            coVerify { activeCardsUseCase.switchActiveCard(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotSwitchActiveCard() {
        viewModel.run {
            coEvery { activeCardsUseCase.switchActiveCard(any()) } returns MockErrorProvider.mockErrorFlow()
            switchActiveCard(mockedCard())
            coVerify { activeCardsUseCase.switchActiveCard(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteCard() {
        viewModel.run {
            coEvery { deleteCardsUseCase.deleteCard(any()) } returns mockedCardsFlow()
            deleteCard(mockedCard())
            coVerify { deleteCardsUseCase.deleteCard(any()) }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteCard() {
        viewModel.run {
            coEvery { deleteCardsUseCase.deleteCard(any()) } returns MockErrorProvider.mockErrorFlow()
            deleteCard(mockedCard())
            coVerify { deleteCardsUseCase.deleteCard(any()) }
            assertNotNull(errorMessage.value)
        }
    }

    @Test
    fun shouldDeleteAllCards() {
        viewModel.run {
            coEvery { deleteCardsUseCase.deleteAllCards() } returns mockedCardsFlow()
            deleteAllCards()
            coVerify { deleteCardsUseCase.deleteAllCards() }
            assertTrue(cards.value?.isNotEmpty().orFalse())
        }
    }

    @Test
    fun shouldNotDeleteAllCards() {
        viewModel.run {
            coEvery { deleteCardsUseCase.deleteAllCards() } returns MockErrorProvider.mockErrorFlow()
            deleteAllCards()
            coVerify { deleteCardsUseCase.deleteAllCards() }
            assertNotNull(errorMessage.value)
        }
    }
}