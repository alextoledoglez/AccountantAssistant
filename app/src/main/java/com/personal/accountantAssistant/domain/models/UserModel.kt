package com.personal.accountantAssistant.domain.models

data class UserModel(
    val name: String,
    val lastName: String,
    val fullName: String,
    val email: String,
    val photoUrl: String
) {
    companion object {
        const val USER_DISPLAY_NAME_KEY = "user_display_name_key"
        const val USER_FULL_NAME_KEY = "user_full_name_key"
        const val USER_EMAIL_KEY = "user_email_key"
    }
}
