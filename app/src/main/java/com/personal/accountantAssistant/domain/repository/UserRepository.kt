package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun setSignedUser(user: UserModel?): Flow<Unit>
    fun getSignedUser(): Flow<UserModel?>
}