package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.domain.models.UserModel

class UserRemoteDataSource(private val storage: LocalStorage) {

    fun setSignedUser(user: UserModel?) = storage.setSignedUser(user)

    fun getSignedUser() = storage.getSignedUser()
}