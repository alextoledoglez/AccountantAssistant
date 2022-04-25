package com.personal.accountantAssistant.domain.models

data class UserModel(
    val name: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val photoPath: String? = null
) {
    companion object {
        const val USER_DISPLAY_NAME_KEY = "user_display_name_key"
        const val USER_FULL_NAME_KEY = "user_full_name_key"
        const val USER_EMAIL_KEY = "user_email_key"
    }
}
