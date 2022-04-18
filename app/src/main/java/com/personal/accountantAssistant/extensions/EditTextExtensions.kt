package com.personal.accountantAssistant.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText?.hideSoftInputOnFocusLoss() {
    this?.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
        hasFocus.not().apply { this@hideSoftInputOnFocusLoss.hideSoftInputFromWindow() }
    }
}

fun EditText?.hideSoftInputFromWindow() {
    val inputService = this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE)
    val inputManager = inputService as InputMethodManager
    inputManager.hideSoftInputFromWindow(this?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    this?.clearFocus()
}