package com.personal.accountantAssistant.utils

import android.content.Context
import androidx.core.app.ActivityCompat.requestPermissions
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.enums.ExpensesFieldsEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.LocaleTypes
import com.personal.accountantAssistant.data.mappers.isBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.DateUtils.toCurrentDateStr
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object ImportExportUtils {

    private const val DB_NAME = AppDatabase.DB_NAME
    private const val DB_BACKUP_FORMAT = "%s"
    private const val FILE_DIRECTORY_TYPE = ""
    private const val FIRST_SHEET = 0
    private const val HEADER_ROW = 0
    private const val BODY_ROW = HEADER_ROW + 1
    private const val TITLE_POINT_SIZE = 16

    fun xlsImport(context: Context?, type: ExpensesType?) {
        //TODO
        print(type)
        context?.showToastShortText(R.string.excel_data_imported)
    }

    fun xlsExport(context: Context, expenses: List<ExpenseModel>?, type: ExpensesType) {
        val storageDirectory = context.getExternalFilesDir(String.EMPTY)
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
                xlsFileName += "(" + toCurrentDateStr() + ").xls"
                sheetName += "_list"
                val xlsFile = File(directory, xlsFileName)
                val wbSettings = WorkbookSettings()
                wbSettings.locale = Locale(LocaleTypes.EN.language, LocaleTypes.EN.name)
                val workbook = Workbook.createWorkbook(xlsFile, wbSettings)
                val sheet = workbook.createSheet(sheetName, FIRST_SHEET)
                fillSheetFrom(expenses, sheet, type)
                workbook.write()
                workbook.close()
                context.showToastShortText(R.string.excel_data_exported)
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }

    fun importDBFrom(context: Context) {
        try {
            if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                val sourceDirectory: File? = context.getExternalFilesDir(FILE_DIRECTORY_TYPE)
                if (sourceDirectory?.canWrite() == true) {
                    val backupDB: File = context.getDatabasePath(DB_NAME)
                    val backupDBPath: String =
                        java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                    val currentDB = File(sourceDirectory, backupDBPath)
                    val src: FileChannel = FileInputStream(currentDB).channel
                    val dst: FileChannel = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    context.showToastLongText(R.string.db_successfully_imported)
                } else {
                    context.showToastLongText(R.string.db_importing_failure)
                }
            } else {
                requestStoragePermissionsFrom(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportDBFrom(context: Context): File? {
        try {
            if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                val currentDB: File = context.getDatabasePath(DB_NAME)
                val src: FileChannel = FileInputStream(currentDB).channel
                val sourceDirectory: File? = context.getExternalFilesDir(FILE_DIRECTORY_TYPE)
                if (sourceDirectory?.canWrite() == true) {
                    val backupDBPath: String =
                        java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                    val backupDB = File(sourceDirectory, backupDBPath)
                    val dst: FileChannel = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    context.showToastLongText(R.string.db_successfully_exported)
                    return backupDB
                } else {
                    context.showToastLongText(R.string.db_exporting_failure)
                }
            } else {
                requestStoragePermissionsFrom(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun fillSheetFrom(
        expenses: List<ExpenseModel>?, sheet: WritableSheet, type: ExpensesType
    ) {
        setHeaderCell(sheet)
        val rowIndex = AtomicInteger(BODY_ROW)
        expenses?.stream()
            ?.filter { type == it.type }?.forEach {
                val currentRowIndex = rowIndex.get()
                addCell(sheet, 0, currentRowIndex, it.name.toString())
                addCell(sheet, 1, currentRowIndex, it.quantity.toString())
                addCell(sheet, 2, currentRowIndex, getConditionalDateValueFrom(it))
                addCell(sheet, 3, currentRowIndex, it.unitaryValue.toString())
                addCell(sheet, 4, currentRowIndex, it.totalValue.toString())
                addCell(sheet, 5, currentRowIndex, it.type.toString())
                addCell(sheet, 6, currentRowIndex, it.isActive.toString())
                rowIndex.getAndIncrement()
            }
    }

    private fun getConditionalDateValueFrom(model: ExpenseModel) =
        if (model.isBill().orFalse()) model.date.toString() else String.DASH_SEPARATOR

    private fun setHeaderCell(sheet: WritableSheet) {
        for ((colIndex, value) in ExpensesFieldsEnum.values().withIndex()) {
            addHeaderCell(sheet, colIndex, value.value)
        }
    }

    private fun addHeaderCell(sheet: WritableSheet, colIndex: Int, cellValue: String) {
        try {
            val cellFont = WritableFont(WritableFont.ARIAL, TITLE_POINT_SIZE)
            cellFont.setBoldStyle(WritableFont.BOLD)
            val cellFormat = WritableCellFormat(cellFont)
            sheet.setColumnView(colIndex, TITLE_POINT_SIZE)
            addCell(sheet, colIndex, HEADER_ROW, cellValue, cellFormat)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    private fun addCell(
        sheet: WritableSheet,
        colIndex: Int,
        rowIndex: Int,
        value: String,
        cellFormat: WritableCellFormat? = null
    ) {
        try {
            val cellLabel: Label = cellFormat?.let {
                Label(colIndex, rowIndex, value, it)
            } ?: run {
                Label(colIndex, rowIndex, value)
            }
            sheet.addCell(cellLabel)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    private fun requestStoragePermissionsFrom(context: Context) {
        requestPermissions(
            context.toActivity(),
            PermissionsUtils.STORAGE_PERMISSIONS,
            PermissionsUtils.STORAGE_PERMISSION_CODE
        )
    }
}