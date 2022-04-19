package com.personal.accountantAssistant.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.LocaleTypes
import com.personal.accountantAssistant.data.mappers.isBill
import com.personal.accountantAssistant.data.mappers.toCalendarSelectionArgs
import com.personal.accountantAssistant.domain.models.CalendarModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.ui.MainActivity
import jxl.Workbook
import jxl.WorkbookSettings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.*

private fun Context.hasPermissionGranted(
    permission: String
) = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.startMainActivity() {
    Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(this)
    }
}

fun Context.startActivity(activityClass: Class<*>?) {
    Intent(this, activityClass).apply { startActivity(this) }
}

fun Context.toActivity() = this as Activity

fun Context.toMainActivity() = this as MainActivity

fun Context.getCompatColor(@ColorRes resColor: Int) = ContextCompat.getColor(this, resColor)

fun Context.getCompatColor(
    condition: Boolean?, @ColorRes trueResColor: Int, @ColorRes falseResColor: Int
) = getCompatColor(if (condition.orFalse()) trueResColor else falseResColor)

fun Context.showToastLongText(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.showToastLongText(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

fun Context.showToastShortText(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToastShortText(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.haveStoragePermissionGranted() = hasPermissionGranted(
    Manifest.permission.READ_EXTERNAL_STORAGE
) && hasPermissionGranted(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

fun Context.isCameraPermissionGranted() = hasPermissionGranted(Manifest.permission.CAMERA)

fun Context.isCalendarWritePermissionGranted() = hasPermissionGranted(
    Manifest.permission.WRITE_CALENDAR
)

fun Context.requestStoragePermissions() {
    ActivityCompat.requestPermissions(
        toActivity(), Manifest::class.STORAGE_PERMISSIONS, Int.STORAGE_PERMISSION_CODE
    )
}

fun Context.deleteCalendarEvents(model: ExpenseModel?) {
    if (isCalendarWritePermissionGranted()) {
        if (contentResolver.alreadyExistCalendarEventFor(model)) {
            contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                CalendarContract::class.SELECTION_FIELDS,
                model.toCalendarSelectionArgs()
            )
        }
    }
}

fun Context.createCalendarEvent(expenseEntity: ExpenseModel?) {
    if (expenseEntity?.isBill().orFalse()) {
        val uri = if (
            isCalendarWritePermissionGranted()
        ) contentResolver.addEvent(expenseEntity) else null
        uri?.lastPathSegment?.let { eventID -> contentResolver.setMultipleReminders(eventID) }
    }
}

fun Context.importDBFrom(
    @StringRes db_successful_imported: Int, @StringRes db_importing_failure: Int
) {
    try {
        if (haveStoragePermissionGranted()) {
            val sourceDirectory = getExternalFilesDir(String.FILE_DIRECTORY_TYPE)
            val res = if (sourceDirectory?.canWrite() == true) {
                val currentDB = File(sourceDirectory, AppDatabase.DB_BACKUP_PATH)
                val backupDB = getDatabasePath(AppDatabase.DB_NAME)
                val src = FileInputStream(currentDB).channel
                FileOutputStream(backupDB).channel.apply {
                    transferFrom(src, Long.ZERO, src.size())
                    src.close()
                    close()
                }
                db_successful_imported
            } else
                db_importing_failure
            showToastLongText(res)
        } else
            requestStoragePermissions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.exportDBFrom(
    @StringRes db_successful_exported: Int, @StringRes db_exporting_failure: Int
): File? {
    try {
        if (haveStoragePermissionGranted()) {
            val currentDB: File = getDatabasePath(AppDatabase.DB_NAME)
            val src: FileChannel = FileInputStream(currentDB).channel
            val sourceDirectory: File? = getExternalFilesDir(String.FILE_DIRECTORY_TYPE)
            if (sourceDirectory?.canWrite() == true) {
                val backupDB = File(sourceDirectory, AppDatabase.DB_BACKUP_PATH)
                FileOutputStream(backupDB).channel.apply {
                    transferFrom(src, Long.ZERO, src.size())
                    src.close()
                    close()
                }
                showToastLongText(db_successful_exported)
                return backupDB
            } else
                showToastLongText(db_exporting_failure)
        } else
            requestStoragePermissions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Context.xlsImport(type: ExpensesType?) {
    //TODO
    print(type)
    showToastShortText(R.string.excel_data_imported)
}

fun Context.xlsExport(expenses: List<ExpenseModel>?, type: ExpensesType) {
    val storageDirectory = getExternalFilesDir(String.EMPTY)
    val directory = storageDirectory?.absolutePath?.let { File(it) }
    val directoryExist = directory?.isDirectory?.not().let {
        directory?.mkdirs()
    }
    directoryExist?.let {
        try {
            val BUYS = "Buys"
            val BILLS = "Bills"
            var xlsFileName = String.EMPTY
            var sheetName = String.EMPTY
            if (ExpensesType.isBuy(type)) {
                xlsFileName = BUYS
                sheetName = BUYS
            } else if (ExpensesType.isBill(type)) {
                xlsFileName = BILLS
                sheetName = BILLS
            }
            xlsFileName += "(" + CalendarModel().toCurrentDateStr() + ").xls"
            sheetName += "_list"
            val xlsFile = File(directory, xlsFileName)
            val wbSettings = WorkbookSettings()
            wbSettings.locale = Locale(LocaleTypes.EN.language, LocaleTypes.EN.name)
            val workbook = Workbook.createWorkbook(xlsFile, wbSettings)
            val sheet = workbook.createSheet(sheetName, Int.FIRST_SHEET)
            sheet.fillSheetFrom(expenses, type)
            workbook.write()
            workbook.close()
            showToastShortText(R.string.excel_data_exported)
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
}

fun Context.importDBFrom() {
    try {
        if (haveStoragePermissionGranted()) {
            val sourceDirectory: File? = getExternalFilesDir(String.FILE_DIRECTORY_TYPE)
            if (sourceDirectory?.canWrite() == true) {
                val backupDB: File = getDatabasePath(AppDatabase.DB_NAME)
                val currentDB = File(sourceDirectory, AppDatabase.DB_BACKUP_PATH)
                val src: FileChannel = FileInputStream(currentDB).channel
                val dst: FileChannel = FileOutputStream(backupDB).channel
                dst.transferFrom(src, Long.ZERO, src.size())
                src.close()
                dst.close()
                showToastLongText(R.string.db_successfully_imported)
            } else {
                showToastLongText(R.string.db_importing_failure)
            }
        } else {
            requestStoragePermissions()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.exportDBFrom(): File? {
    try {
        if (haveStoragePermissionGranted()) {
            val currentDB: File = getDatabasePath(AppDatabase.DB_NAME)
            val src: FileChannel = FileInputStream(currentDB).channel
            val sourceDirectory: File? = getExternalFilesDir(String.FILE_DIRECTORY_TYPE)
            if (sourceDirectory?.canWrite() == true) {
                val backupDB = File(sourceDirectory, AppDatabase.DB_BACKUP_PATH)
                val dst: FileChannel = FileOutputStream(backupDB).channel
                dst.transferFrom(src, Long.ZERO, src.size())
                src.close()
                dst.close()
                showToastLongText(R.string.db_successfully_exported)
                return backupDB
            } else {
                showToastLongText(R.string.db_exporting_failure)
            }
        } else {
            requestStoragePermissions()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Context.scan(): String {
    val result = String.EMPTY
    val detector = BarcodeDetector.Builder(this)
        .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODABAR or Barcode.QR_CODE)
        .build()
    if (!detector.isOperational) {
        this.showToastShortText(R.string.fail_bar_code_detector)
    } else {
        /*           final SparseArray<Barcode> barCodes = detector.detect(new Frame());
        final Barcode thisCode = barCodes.valueAt(0);
        result = thisCode.rawValue;*/
    }
    return result
}
