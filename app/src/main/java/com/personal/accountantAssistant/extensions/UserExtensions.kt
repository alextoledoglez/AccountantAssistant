package com.personal.accountantAssistant.extensions

import com.personal.accountantAssistant.domain.models.UserModel

fun UserModel?.orEmpty() = this ?: UserModel()