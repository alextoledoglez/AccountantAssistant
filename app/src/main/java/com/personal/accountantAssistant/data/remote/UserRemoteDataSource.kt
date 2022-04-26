package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orEmpty
import kotlinx.coroutines.flow.Flow

class UserRemoteDataSource(private val storage: LocalStorage?) {

    fun setSignedUser(user: UserModel?): Flow<Unit> = flowEmit {
        storage?.putObject(LocalStorage.SIGNED_USER, user.orEmpty())
    }

    fun getSignedUser(): Flow<UserModel?> = flowEmit {
        storage?.getObject(LocalStorage.SIGNED_USER, UserModel::class)
    }
}