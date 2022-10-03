package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SetSignedUserUseCaseImpl(val repository: UserRepository) : SetSignedUserUseCase {
    override fun invoke(user: UserModel?): Flow<Boolean?> = repository.setSignedUser(user)
}