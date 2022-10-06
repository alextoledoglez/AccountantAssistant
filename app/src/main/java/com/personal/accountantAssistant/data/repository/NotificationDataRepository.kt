package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.LocalStorage
import com.personal.accountantAssistant.data.enums.NotificationTopics.GENERAL
import com.personal.accountantAssistant.domain.repository.NotificationRepository
import com.personal.accountantAssistant.providers.NotificationProvider

class NotificationDataRepository(
    private val storage: LocalStorage, private val provider: NotificationProvider
) : NotificationRepository {
    override fun setLocalNotificationToken(token: String?) = storage.setNotificationToken(token)
    override fun getLocalNotificationToken() = storage.getNotificationToken()
    override fun getNotificationToken() = provider.getToken()
    override fun subscribeNotificationTopic() = provider.subscribeToTopic(GENERAL.topic)
}