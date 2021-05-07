package com.personal.accountantAssistant.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.ActionUtils.runAction
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicInteger

object DialogUtils {

    fun confirmationDialog(
        context: Context,
        titleId: Int,
        messageId: Int,
        confirmAction: Action
    ) {
        confirmationDialog(
            context,
            context.getString(titleId),
            context.getString(messageId),
            confirmAction,
            ActionUtils.NONE_ACTION_TO_DO
        )
    }

    private fun confirmationDialog(
        context: Context,
        title: String,
        message: String,
        confirmAction: Action,
        cancelAction: Action
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int -> runAction(confirmAction) }
            .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int ->
                runAction(
                    cancelAction
                )
            }
            .show()
    }

    fun showImportExportDialog(
        context: Context?,
        titleId: Int,
        importAction: Action,
        exportAction: Action
    ) {
        context?.let {
            showImportExportDialog(
                it,
                it.getString(titleId),
                importAction,
                exportAction
            )
        }
    }

    private fun showImportExportDialog(
        context: Context,
        title: String,
        importAction: Action,
        exportAction: Action
    ) {
        val importOption = 0
        val exportOption = 1
        val options = arrayOf(
            context.getString(R.string.import_all),
            context.getString(R.string.export_all)
        )
        val checkedItem = AtomicInteger(0)
        AlertDialog.Builder(context)
            .setTitle(title)
            .setSingleChoiceItems(
                options,
                checkedItem.get()
            ) { _: DialogInterface?, which: Int -> checkedItem.set(which) }
            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                when (checkedItem.get()) {
                    importOption -> runAction(importAction)
                    exportOption -> runAction(exportAction)
                }
            }
            .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> }
            .show()
    }
}