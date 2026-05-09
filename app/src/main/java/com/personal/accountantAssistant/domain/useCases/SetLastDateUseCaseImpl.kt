package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.data.LocalStorage
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SetLastDateUseCaseImpl(private val storage: LocalStorage) : SetLastDateUseCase {

    override fun invoke(value: Date?): Flow<Unit> = storage.setLastDate(value)
}