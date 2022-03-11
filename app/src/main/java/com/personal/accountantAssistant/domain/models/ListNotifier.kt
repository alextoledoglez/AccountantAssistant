package com.personal.accountantAssistant.domain.models

import com.personal.accountantAssistant.data.enums.ListNotifyTypes

data class ListNotifier(val type: ListNotifyTypes, val position: Int)