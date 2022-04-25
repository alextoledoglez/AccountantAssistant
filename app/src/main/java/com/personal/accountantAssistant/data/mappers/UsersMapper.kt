package com.personal.accountantAssistant.data.mappers

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.personal.accountantAssistant.domain.models.UserModel

fun GoogleSignInAccount.toUserModel() = UserModel(
    name = givenName.orEmpty(),
    lastName = familyName.orEmpty(),
    fullName = displayName.orEmpty(),
    email = email.orEmpty(),
    photoPath = photoUrl?.toString()
)