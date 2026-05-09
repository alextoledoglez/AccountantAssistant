package com.personal.accountantAssistant.domain.useCases.home

import com.personal.accountantAssistant.data.LocalStorage
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SetPeriodDatesUseCaseImpl(private val storage: LocalStorage) : SetPeriodDatesUseCase {

    override fun invoke(first: Date?, last: Date?): Flow<Unit> = storage.setPeriodDates(first, last)
}