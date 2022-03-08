package com.personal.accountantAssistant.bases

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.LayoutDialogTitleBinding

class AlertDialogBuilder(context: Context) :
    MaterialAlertDialogBuilder(context, R.style.AlertDialogBuilderStyle) {

    private val customTitleBinding = LayoutDialogTitleBinding.inflate(LayoutInflater.from(context))

    init {
        setCustomTitle(customTitleBinding.root)
    }

    override fun setTitle(title: CharSequence?): MaterialAlertDialogBuilder {
        customTitleBinding.tvAlertDialogTitle.text = title
        return this
    }

    override fun setTitle(titleId: Int): MaterialAlertDialogBuilder {
        customTitleBinding.tvAlertDialogTitle.setText(titleId)
        return this
    }

    fun setOkButtonAction(action: () -> Unit): AlertDialog.Builder = setPositiveButton(R.string.ok)
    { _: DialogInterface?, _: Int -> action() }

    fun setCancelButtonAction(action: () -> Unit): AlertDialog.Builder =
        setNegativeButton(R.string.cancel)
        { _: DialogInterface?, _: Int -> action() }

    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 100

        fun toCurrentOrMinValue(value: Int) = (if (value == 0) MIN_VALUE else value)
    }
}