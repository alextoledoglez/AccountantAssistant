package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.remote.UserRemoteDataSource
import com.personal.accountantAssistant.domain.models.UserModel
import com.personal.accountantAssistant.domain.repository.UserRepository

class UserDataRepository(
    private val dataSource: UserRemoteDataSource
) : UserRepository {

    override fun setSignedUser(user: UserModel?) = dataSource.setSignedUser(user)
    override fun getSignedUser() = dataSource.getSignedUser()
}