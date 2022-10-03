package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.extensions.flowEmit

class UserRemoteDataSource(private val storage: LocalStorage?) {

    fun setSignedUser(user: UserModel?) = flowEmit {
        storage?.putObject(LocalStorage.SIGNED_USER, user)
    }

    fun getSignedUser() = flowEmit {
        storage?.getObject(LocalStorage.SIGNED_USER, UserModel::class)
    }
}