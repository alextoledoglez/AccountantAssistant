package com.personal.accountantAssistant.bases.adapters

class ListAdapterChanges<T>(
    val onEdit: (model: T) -> Unit,
    val onActive: (model: T) -> Unit,
    val onRemove: (model: T) -> Unit,
)