package com.personal.accountantAssistant.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.personal.accountantAssistant.R;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.functions.Action;

public class DialogUtils {

    public static void confirmationDialog(final Context context,
                                          final int titleId,
                                          final int messageId,
                                          final Action confirmAction) {
        confirmationDialog(context,
                context.getString(titleId),
                context.getString(messageId),
                confirmAction,
                ActionUtils.NONE_ACTION_TO_DO);
    }

    private static void confirmationDialog(final Context context,
                                           final String title,
                                           final String message,
                                           final Action confirmAction,
                                           final Action cancelAction) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, id) -> ActionUtils.runAction(confirmAction))
                .setNegativeButton(R.string.cancel, (dialog, id) -> ActionUtils.runAction(cancelAction))
                .show();
    }

    public static void showImportExportDialog(final Context context,
                                              final int titleId,
                                              final Action importAction,
                                              final Action exportAction) {
        showImportExportDialog(context,
                context.getString(titleId),
                importAction,
                exportAction);
    }

    private static void showImportExportDialog(final Context context,
                                               final String title,
                                               final Action importAction,
                                               final Action exportAction) {
        final int IMPORT_OPTION = 0;
        final int EXPORT_OPTION = 1;
        final String[] options = {
                context.getString(R.string.import_all),
                context.getString(R.string.export_all)
        };
        final AtomicInteger checkedItem = new AtomicInteger(0);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(options, checkedItem.get(), (dialog, which) -> checkedItem.set(which))
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    switch (checkedItem.get()) {
                        case IMPORT_OPTION:
                            ActionUtils.runAction(importAction);
                            break;
                        case EXPORT_OPTION:
                            ActionUtils.runAction(exportAction);
                            break;
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                })
                .show();
    }
}
