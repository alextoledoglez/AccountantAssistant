package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orCurrent
import com.personal.accountantAssistant.extensions.toDate
import kotlinx.coroutines.flow.Flow
import java.util.*

class GetFirstDateUseCaseImpl(private val storage: LocalStorage?) : GetFirstDateUseCase {

    override fun invoke(): Flow<Date?> = flowEmit {
        storage?.getString(LocalStorage.FIRST_STR_DATE, String.EMPTY)?.toDate().orCurrent()
    }
}