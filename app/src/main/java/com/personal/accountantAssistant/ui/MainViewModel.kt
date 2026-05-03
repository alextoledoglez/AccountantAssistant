package com.personal.accountantAssistant.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _showDeleteAllDialog = MutableStateFlow(false)
    val showDeleteAllDialog: StateFlow<Boolean> = _showDeleteAllDialog.asStateFlow()

    fun showDeleteDialog() {
        _showDeleteAllDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteAllDialog.value = false
    }
}