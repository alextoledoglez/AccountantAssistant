package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.NotificationEntity
import com.personal.accountantAssistant.domain.models.NotificationModel

fun NotificationEntity.toModel() = NotificationModel(
    title = title,
    subtitle = subtitle
)

fun NotificationModel.toEntity() = NotificationEntity(
    title = title,
    subtitle = subtitle
)