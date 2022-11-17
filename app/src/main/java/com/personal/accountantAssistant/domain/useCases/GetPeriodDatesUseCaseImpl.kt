package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetPeriodDatesUseCaseImpl(private val storage: LocalStorage?) : GetPeriodDatesUseCase {

    override fun invoke(): Flow<Pair<Date?, Date?>> = flowEmit {
        Pair(first = getFirstDate(), second = getSecondDate())
    }

    private fun getFirstDate() =
        storage?.getString(LocalStorage.FIRST_STR_DATE, String.EMPTY)?.toDate().orCurrent()

    private fun getSecondDate() =
        storage?.getString(LocalStorage.LAST_STR_DATE, String.EMPTY)?.toDate() ?: run {
            val nextMonth = Calendar.getInstance()
            nextMonth.add(Calendar.MONTH, Int.ONE)
            nextMonth.time
        }
}