package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface SetSignedUserUseCase {
    operator fun invoke(user: UserModel?): Flow<Boolean?>
}