package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.flow.Flow

class UserRemoteDataSource(private val storage: LocalStorage?) {

    fun setSignedUser(user: UserModel?): Flow<Boolean?> = flowEmit {
        storage?.putObject(LocalStorage.SIGNED_USER, user)
    }

    fun getSignedUser(): Flow<UserModel?> = flowEmit {
        storage?.getObject(LocalStorage.SIGNED_USER, UserModel::class)
    }
}