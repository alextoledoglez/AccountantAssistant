package com.personal.accountantAssistant.domain.models

data class UserModel(
    val name: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val photoPath: String? = null
) {
    companion object {
        const val USER_DISPLAY_NAME_KEY = "USER_DISPLAY_NAME_KEY"
        const val USER_FULL_NAME_KEY = "USER_FULL_NAME_KEY"
        const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
    }
}
