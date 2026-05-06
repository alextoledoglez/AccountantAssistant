package com.personal.accountantAssistant.domain.useCases.home

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetPeriodDatesUseCaseImpl(private val storage: LocalStorage) : GetPeriodDatesUseCase {

    override fun invoke(): Flow<Pair<Date?, Date?>> = flowEmit {
        Pair(first = storage.getFirstDate(), second = storage.getSecondDate())
    }
}