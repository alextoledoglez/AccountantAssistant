package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetSignedUserUseCaseImpl(val repository: UserRepository) : GetSignedUserUseCase {
    override fun invoke(): Flow<UserModel?> = repository.getSignedUser()
}