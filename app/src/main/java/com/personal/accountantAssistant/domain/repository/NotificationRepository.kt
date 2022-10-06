package com.personal.accountantAssistant.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun setLocalNotificationToken(token: String?): Flow<Boolean?>
    fun getLocalNotificationToken(): Flow<String?>
    fun getNotificationToken(): Flow<String?>
    fun subscribeNotificationTopic(): Flow<Void?>
}