package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SetFirstDateUseCaseImpl(private val storage: LocalStorage) : SetFirstDateUseCase {

    override fun invoke(value: Date?): Flow<Unit> = storage.setFirstDate(value)
}