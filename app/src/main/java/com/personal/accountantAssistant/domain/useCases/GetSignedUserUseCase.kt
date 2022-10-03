package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface GetSignedUserUseCase {
    operator fun invoke(): Flow<UserModel?>
}